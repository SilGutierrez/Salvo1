$(function() {

  // display text in the output area
  function showOutput(text) {
    $("#output").text(text);
  }

  // load and display JSON sent by server for /players

  function loadData() {
    $.get("/api/games")
    .done(function(data) {
      showOutput(JSON.stringify(data, null, 2));
      tabla_games(data);
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }

  // handler for when user clicks add person

  function addPlayer() {
    var name = $("#email").val();
    if (name) {
      postPlayer(name);
    }
  }

  // code to post a new player using AJAX
  // on success, reload and display the updated data from the server

  function postPlayer(userName) {
    $.post({
      headers: {
          'Content-Type': 'application/json'
      },
      dataType: "text",
      url: "/players",
      data: JSON.stringify({ "userName": userName })
    })
    .done(function() {
      showOutput( "Saved -- reloading");
      loadData();
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }

  $("#add_player").on("click", addPlayer);

  loadData();
});

      function tabla_games(data){

    console.log("data ->" + data);
    console.log("length ->" + data.length);

      for (let i = 0; i < data.length; i++){

      console.log(i);

        var fila= document.createElement("tr")
        var gameId= document.createElement("td")
        var created= document.createElement("td")
        var playerId= document.createElement("td")
        var email= document.createElement("td")
        var playerIdEnemy = document.createElement("td")
        var emailEnemy = document.createElement("td")

        gameId.innerHTML=data[i].gameId;
        created.innerHTML=data[i].created;

        playerId.innerHTML=data[i].gamePlayers[0].player.playerId;
        email.innerHTML=data[i].gamePlayers[0].player.email;

        if(data[i].gamePlayers[1].player){
            playerIdEnemy.innerHTML=data[i].gamePlayers[1].player.playerId;
            emailEnemy.innerHTML=data[i].gamePlayers[1].player.email;
        }



        fila.appendChild(gameId)
        fila.appendChild(created)
        fila.appendChild(playerId)
        fila.appendChild(email)
        fila.appendChild(playerIdEnemy)
        fila.appendChild(emailEnemy)



        document.getElementById("gametable").appendChild(fila)


        }


      }
