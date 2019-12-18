let gp = getParameterByName('gp')
var jugador = {
    username: '',
    id: ''
};
var opositor = {
    username: '',
    id: ''
};

var oponentObject;
var localObject;

fetch("/api/game_view/" + gp)
    .then(function (res) {
        if (res.ok) {
            return res.json()
        }
    })
    .then(function (json) {
        console.log(json);
        data(json.gamePlayer)
        ship(json.ship);
        printSalvo(json);
    })
    .catch(ex => console.log(ex));

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

/* PRINT DATA */
function printSalvo(json) {
    let ships = [];
    let hitAux = [];

    for (let i = 0; i < json.hits.locations.length; i++) {
        hitAux.push(json.hits.locations[i]);
    }

    //document.getElementById(`salvo${position}`).style.backgroundColor = "red";
    for (let i = 0; i < json.ship.length; i++) {
        json.ship[i].locations.forEach(element => {
            ships.push(element);
        })
    }

    for (let i = 0; i < json.salvoes.length; i++) {
        let aux = json.salvoes[i];
        if (aux.player != jugador.id) {
            aux.locations.forEach(element => {
                if (ships.includes(element)) {
                    document.getElementById(`ships${element}`).style.backgroundColor = "blue";
                }
            });
        } else {
            aux.locations.forEach(element => {
                if (hitAux.includes(element)) {
                    document.getElementById(`salvo${element}`).style.backgroundColor = "red";
                } else {
                    document.getElementById(`salvo${element}`).style.backgroundColor = "pink";
                }
            });
        }
    }


}

function ship(json) {
    if (json.length != 0) {
        for (let i = 0; i < json.length; i++) {
            let location = json[i].locations;
            let firstLocation = location[0];
            let secondLocation = location[1];
            if (firstLocation[0] == secondLocation[0]) {
                createShips(json[i].type, location.length, 'horizontal', document.getElementById('ships' + location[0]), true);
            } else {
                createShips(json[i].type, location.length, 'vertical', document.getElementById('ships' + location[0]), true);
            }
        }
    } else {
        createShips('carrier', 5, 'horizontal', document.getElementById('dock'), false)
        createShips('battleship', 4, 'horizontal', document.getElementById('dock'), false)
        createShips('submarine', 3, 'horizontal', document.getElementById('dock'), false)
        createShips('destroyer', 3, 'horizontal', document.getElementById('dock'), false)
        createShips('patrol_boat', 2, 'horizontal', document.getElementById('dock'), false)
    }

}

function data(json) {
    for (let i = 0; i < json.length; i++) {
        if (json[i].gamePlayerId == gp) {
            let aux = json[i].player;
            jugador.username = aux.userName;
            jugador.id = aux.playerId;
            createGamePlayer(aux.userName, 'jugador1', document.getElementById("jugador"));
        } else {
            let aux = json[i].player;
            opositor.username = aux.userName;
            opositor.id = aux.playerId;
            createGamePlayer(aux.userName, 'opositor', document.getElementById("opositor"));
        }
    }
}

function createGamePlayer(email, text, element) {
    element.innerHTML = text + ": " + email
}


/* BUTTONS */
document.getElementById("collect-boats").addEventListener("click", function () {
    collectShips();
    sendShips();
});

document.getElementById("collect-salvos").addEventListener("click", function () {
    shoot(salvoArray);
});


/* SHIPS */
var shipsLocated = [];
const ships = ['carrier', 'battleship', 'submarine', 'destroyer', 'patrol_boat'];

function collectShips() {
    for (let i = 0; i < ships.length; i++) {
        let shipObject = {
            type: "",
            locations: []
        }
        let ship = document.getElementById(ships[i]);
        if (ship.dataset.y != undefined && ship.dataset.x != undefined) {
            shipObject.type = ship.id;

            let config = document.getElementsByClassName(`${ships[i]}-busy-cell`);

            for (let i = 0; i < config.length; i++) {
                shipObject.locations.push(config[i].dataset.y + config[i].dataset.x);
            }
            shipsLocated.push(shipObject);
        }
    }
}

function sendShips() {
    if (shipsLocated.length == 5) {
        var idGamePlayer = getParameterByName('gp');
        fetch(`/api/games/players/${idGamePlayer}/ships`, {
                method: 'POST',
                body: JSON.stringify(shipsLocated),
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(function (response) {
                if (response.ok) {
                    location.reload();
                }
                throw new Error(response);
            })
            .catch(ex => console.log(ex));
    }
}


/* SALVOS */
function shoot(shots) {
    var idGamePlayer = getParameterByName('gp');
    let url = '/api/games/players/' + idGamePlayer + '/salvoes'
    let init = {
        method: 'POST',
        headers: {
            "content-type": "application/json"
        },
        body: JSON.stringify(shots)
    }
    fetch(url, init)
        .then(res => {
            if (res.ok) {
                location.reload();
            } else {
                return Promise.reject(res.json())
            }

        })
        .catch(error => error)
        .then(error => console.log(error))
}

var salvoArray = [];
$("#salvoGrid .grid-cell").click(function (evt) {
    if (salvoArray.length < 5) {
        let clicked = evt.currentTarget;
        clicked.style.backgroundColor = "green";
        let location = clicked.dataset.y + clicked.dataset.x;
        if (salvoArray.includes(location)) {
            alert("Posicion ya incluida")
        } else {
            //document.getElementById("positionList").innerHTML += `<li class="collection-item">${location}</li>`;
            salvoArray.push(location);
        }
    }
    console.log(salvoArray.length);
    if (salvoArray.length == 5) {
        console.log(salvoArray);
        document.getElementById("collect-salvos").disabled = false;
    }
});