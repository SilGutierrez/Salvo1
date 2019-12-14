function loguearse() {
  let formdata = new FormData(document.querySelector("#forms-users"))
  fetch('/api/login', {
    method: 'POST',
    body: formdata
  }).then(function (response) {
    if (response.ok) {
      console.log("nice")
    }
  })
}

function logout() {
  fetch('/api/logout', {
    method: 'POST',
  }).then(function (response) {
    if (response.ok) {
      console.log("nice")
    }
  })
}

function registrarse() {
  let formdata = new FormData(document.querySelector("#forms-users"))
  fetch('/api/players', {
    method: 'POST',
    body: formdata
  }).then(function (response) {
    if (response.ok) {
      loguearse()
      console.log("nice")
    } else {
      console.log(response.text())
    }
  })
}

var userData = '';

$(function () {

  // display text in the output area
  function showOutput(text) {
    $("#output").text(text);
  }

  // load and display JSON sent by server for /players

  function loadData() {
    $.get("/api/games")
      .done(function (data) {
        showOutput(JSON.stringify(data, null, 2));
        tabla_games(data);
        chargeTableGames(data);
      })
      .fail(function (jqXHR, textStatus) {
        showOutput("Failed: " + textStatus);
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
        url: "/api/players",
        data: JSON.stringify({
          "userName": userName
        })
      })
      .done(function () {
        showOutput("Saved -- reloading");
        loadData();
      })
      .fail(function (jqXHR, textStatus) {
        showOutput("Failed: " + textStatus);
      });
  }

  $("#add_player").on("click", addPlayer);

  loadData();
});

function tabla_games(object) {

  let data = object.games;

  for (let i = 0; i < data.length; i++) {

    let fila = document.createElement("tr");
    let gameId = document.createElement("td");
    let created = document.createElement("td");
    let playerId = document.createElement("td");
    let email = document.createElement("td");
    let playerIdEnemy = document.createElement("td");
    let emailEnemy = document.createElement("td");

    gameId.innerHTML = data[i].gameId;
    created.innerHTML = data[i].created;

    playerId.innerHTML = data[i].gamePlayers[0].player.playerId;
    email.innerHTML = data[i].gamePlayers[0].player.email;

    if (data[i].gamePlayers) {
      if (data[i].gamePlayers[1]) {
        playerIdEnemy.innerHTML = data[i].gamePlayers[1].player.playerId;
        emailEnemy.innerHTML = data[i].gamePlayers[1].player.email;
      }
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

function searchGameStatus(data) {
  let object = {
    can: false,
    message: 'cannot'
  }
  if (data.length < 2 && data.length > 0) {
    object.can = true;

  }
  return object;
}

function chargeTableGames(data) {
  let games = data.games;
  let user = data.player;
  if (user != "guest") {
    const table = document.getElementById("game_list")
    userData = user;

    let html = '';
    console.log(userData);

    games.forEach(element => {
      html += '<tr>';
      element.gamePlayers.forEach((aux, index) => {
        html += `<td>${aux.player.userName}</td>`
        if (index == 0) {
          html += `<td>vs</td>`
        }
      })
      element.gamePlayers.forEach(aux => {
        if (aux.player.playerId == userData.playerId) {
          if (element.gamePlayers.length == 2) {
            html += `<td><button onclick="sendTo(${aux.gamePlayerId})">Ingresar</button></td>`
          }
          if (element.gamePlayers.length < 2) {
            html += `<td><button onclick="sendTo(${aux.gamePlayerId})">Entrar</button></td>`
          }
        } else {
          if (element.gamePlayers.length < 2) {
            html += `<td><button onclick="intoGame(${element.gameId})">Unirse</button></td>`
          }
        }
      })
      html += '</tr>';
    });

    table.innerHTML = html;

  }
}

function sendTo(link) {
  window.location = `game.html?gp=${link}`
}

function intoGame(gameId) {
  console.log(gameId);
  fetch(`/api/games/${gameId}/players`, {
      method: 'POST'
    })
    .then(function (data) {
      console.log(data);
      if (data.ok) {
        return data.json();
      } else {
        console.log("Error");
      }
    })
    .then(function (response) {
      window.location = `game.html?gp=${response.id}`;
    })
    .catch(function (ex) {
      console.log(ex);
    })

}