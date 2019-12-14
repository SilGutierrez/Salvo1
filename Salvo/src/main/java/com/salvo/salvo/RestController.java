package com.salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;


    // Auth
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/api/games")
    public Map<String,Object> getAllGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if(isGuest(authentication)){
            dto.put("player","guest");
        }else{
            dto.put("player",playerRepository.findPlayerByUserName(authentication.getName()).getPlayerData());
        }
        dto.put("games",gameRepository.findAll().stream().map(Game::getGameData).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;

        if (isGuest(authentication)) {
            response = new ResponseEntity(makeMap("error", "You must logged in first"), HttpStatus.UNAUTHORIZED);
        } else {

            Player player = playerRepository.findPlayerByUserName(authentication.getName());
            Game newGame = gameRepository.save(new Game());
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(player, newGame));


            response = new ResponseEntity(makeMap("gpId", String.valueOf(newGamePlayer.getId())), HttpStatus.CREATED);
        }
        return response;

    }

    private Object makeMap(String error, String you_must_logged_in_first) {
    return null;
    }


    @RequestMapping("/api/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId){
        return gamePlayerRepository.findById(gamePlayerId).map(this::gameViewDto).orElse(null);
    }

    public Map<String, Object> gameViewDto(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();

        if(gamePlayer !=null){
            dto.put("id", gamePlayer.getGame().getId());
            dto.put("creationDate", gamePlayer.getGame().getDate());
            dto.put("gamePlayer", gamePlayer.getGame().getGamePlayerSet().stream().map(GamePlayer::getGamePlayerData));
            dto.put("ship", gamePlayer.getShipSet().stream().map(Ship::getShipData));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayerSet().stream().flatMap(gp-> gp.getSalvoes().stream().map(Salvo::salvoDto)));
        }else{
            dto.put("error", "no such game");

        }

        return dto;

    }

    //registrar players
    @RequestMapping(path = "/api/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(@RequestParam String username, @RequestParam String password) {

        Map<String, Object> respuesta = new HashMap<>();
        if (username.isEmpty() || password.isEmpty()) {
            respuesta.put("error","Missing data");
            return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findPlayerByUserName(username) !=  null) {
            respuesta.put("error","Name already in use");
            return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}