let gp = getParameterByName('gp')

let jugador = '';
let opositor = '';

var oponentObject;
var localObject;

fetch("/api/game_view/"+gp)
.then(function(res){
    if(res.ok){
        return res.json()
    }
})
.then(function(json){
    console.log(json);
    data(json.gamePlayer)
    ship(json.ship);
    salvo(json.salvoes);
})

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

function salvo(json){
for (let i = 0; i < json.length; i++){

    if(json[i].player == jugador.playerId){
    let location = json[i].locations;

    json[i].locations.forEach(element => {
       console.log(element);
       document.getElementById(`salvo${element}`).style.backgroundColor="red";
    });
    }else{
        let location = json[i].locations;

        json[i].locations.forEach(element => {
           console.log(element);
           document.getElementById(`ships${element}`).style.backgroundColor="red";
        });
    }



    }
}

function ship(json){

   for (let i = 0; i < json.length; i++){
        console.log(json[i])
        let location = json[i].locations;
        let firstLocation = location[0];
        let secondLocation = location[1];
        if (firstLocation[0] == secondLocation[0]){
            createShips(json[i].type,location.length,'horizontal', document.getElementById('ships' + location[0]),true);
        } else{
           createShips(json[i].type , location.length , 'vertical' ,  document.getElementById('ships' + location[0]) , true);
        }

   }
}

        function data (json){

                for (let i = 0; i < json.length; i++){

                if(json[i].gamePlayerId == gp){
                jugador = json[i].player
                createGamePlayer(json[i].player.email, 'jugador1',document.getElementById("jugador"));

                }else{
                opositor = json[i].player
                createGamePlayer(json[i].player.email, 'opositor',document.getElementById("opositor"));
                }

           }
        }


        function createGamePlayer(email,text,element){
            element.innerHTML = text + ": " + email
        }



