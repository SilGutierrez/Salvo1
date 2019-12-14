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

             function scoreTable(scores){

                        let info = document.getElementById('game_score')
                        let table = ""
                                        table = `<table class="table table-bordered text-light">
                                                <thead class="thead-dark">
                                                    <tr>
                                                        <th>Name</th>
                                                        <th>Total</th>
                                                        <th>Won</th>
                                                        <th>Lost</th>
                                                        <th>Tied</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                `

                        for(let i = 0; i < scores.length; i++) {
                               table +=  `<tr>
                                <td>${scores[i].name} </td>
                                <td>${scores[i].total} </td>
                                <td>${scores[i].won} </td>
                                <td>${scores[i].lost} </td>
                                <td>${scores[i].tied} </td>
                                </tr>
                                `
                        }
                    	table += "</tbody> </table>"
                        info.innerHTML = table
                    }




         function table(){
             	    console.log(data)
             		let info = document.getElementById('table')
                     let table = ""
                                     table = `<table class="table table-bordered text-light">

             		                 <thead class="thead-dark">
             		                    <tr>
             		                        <th>Game ID</th>
             								<th>Creation Date</th>
             								<th>Players</th>
             						   </tr>
             						 </thead>
             						 <tbody>`
             		  for(let i = 0; i < data.length; i++){

             		  let gpid = null
             		  let gameId = null

             		    if(player == "guest" || (data[i].gameplayers.length == 2 && data[i].gameplayers.every(e => e.player.id_player != player.id_player))){
                             table +=  `<tr>
                                 <td>${data[i].id}</td>
                                 <td>${data[i].creationDate}</td>
                                 <td>${data[i].gameplayers.map(e => e.player.username).join(" <br /> ")}</td>
                             </tr>
                             `
             		    }else if(data[i].gameplayers.some(e => e.player.id_player == player.id_player)){
             		        gpid = data[i].gameplayers.filter(e => e.player.id_player == player.id_player)[0].id_gamePlayer
             		        table +=  `<tr>
                                 <td>${data[i].id}</td>
                                 <td>${data[i].creationDate}</td>
                                 <td>${data[i].gameplayers.map(e => e.player.username).join(" <br /> ")}</td>
                                 <td><button onclick="enterGame(${gpid})">Enter</button></td>
                             </tr>
                          `
             		    }else if(data[i].gameplayers.length == 1){
             		        gameId = data[i].id
             		        table +=  `<tr>
                                 <td>${data[i].id}</td>
                                 <td>${data[i].creationDate}</td>
                                 <td>${data[i].gameplayers.map(e => e.player.username).join(" <br /> ")}</td>
                                 <td><button onclick="joinGame(${gameId})">Join Game</button></td>
                             </tr>
                             `
             		    }
             		  }
             		  table += "</tbody> </table>"
                       info.innerHTML = table
                  }

                       function enterGame(gpid){
                              window.location.href='/web/game.html?gp='+gpid
                           }