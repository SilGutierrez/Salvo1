let gp = getParameterByName('gp')

let jugador = '';
let opositor = '';

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
        salvo(json.salvoes);
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

function salvo(json) {
    for (let i = 0; i < json.length; i++) {

        if (json[i].player == jugador.playerId) {
            let location = json[i].locations;

            json[i].locations.forEach(element => {
                console.log(element);
                document.getElementById(`salvo${element}`).style.backgroundColor = "red";
            });
        } else {
            let location = json[i].locations;

            json[i].locations.forEach(element => {
                console.log(element);
                document.getElementById(`ships${element}`).style.backgroundColor = "red";
            });
        }



    }
}

function ship(json) {

    if (json.length != 0) {
        for (let i = 0; i < json.length; i++) {
            console.log(json[i])
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
            jugador = json[i].player
            createGamePlayer(json[i].player.email, 'jugador1', document.getElementById("jugador"));

        } else {
            opositor = json[i].player
            createGamePlayer(json[i].player.email, 'opositor', document.getElementById("opositor"));
        }

    }
}

function createGamePlayer(email, text, element) {
    element.innerHTML = text + ": " + email
}

const ships = ['carrier', 'battleship', 'submarine', 'destroyer', 'patrol_boat'];

var shipsLocated = [];
document.getElementById("collect-boats").addEventListener("click", function () {
    collectShips();
    sendShips();
});

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

      function shoot (shots, gamePlayerId){
      let url='/api/games/players/' + gamePlayerId +'/salvoes'
      let init={
      method:'Post',
      herders:{
      "content-type": "application/json"
      },
      body: JSON.stringify(shots)
      }
      fetch (url, init)
      .then (res => {
      if (res.ok){
      return res.json()
      } else{
      return Promise.reject(res.json())
      }

      })

      .then (json => {
      getGameDta (gp)
      })
      .catch (error => error)
      .then (error => console.log (error))
      }