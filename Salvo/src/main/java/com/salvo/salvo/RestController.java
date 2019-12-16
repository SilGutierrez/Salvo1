package com.salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
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

    // task 3 //

     @RequestMapping (path ="/games/players/{gamePlayerId}/Ship", method = RequestMethod.POST)
     public ResponseEntity<Map<String, Object>> addShips(Authentication authentication, @PathVariable long gamePlayer, @RequestBody List<Ship>{
         ResponseEntity <Map<String, Object>> response;
      if (isGuest(authentication)){
          response = new ResponseEntity<>(makeMap("error", "you must be loggedin"), HttpStatus.UNAUTHORIZED);
      } else{
          GamePlayer gamePlayer = gamePlayerRepository.findAllById(gamePlayerId).orElse (null);
          Player player = playerRepository.findPlayerByUserName(authentication.getName());
          if (gamePlayer == null){
              response = new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.);
          } else if (gamePlayer.getPlayer().getId()!=player.getId()){
              response = new ResponseEntity<>(makeMap("error", "this is not your game"), HttpStatus.UNAUTHORIZED);
          } else if (gamePlayer.getShips().size()>0){
              response = new ResponseEntity<>(makeMap("error", "you already have ships"), HttpStatus.FORBIDDEN);
          } else if (Ship = null || ships.size() !=5){
              response = new ResponseEntity<>(makeMap("error", "you must add 5 ships"), HttpStatus.FORBIDDEN);
          } else{
              if(Ships.stream().anyMatch(ship -> this,isOutOfRange (ship))){
                  response = new ResponseEntity<>(makeMap("error", "you have ships out of range"), HttpStatus.FORBIDDEN);
              } else if (Ships.stream().anyMatch(ship -> this.isNotConsecutive(ship))){
                  response = new ResponseEntity<>(makeMap("error", "your ships are not consecutive"), HttpStatus.FORBIDDEN);
              } else if (this.areOverlapped (ships)){
                  response = new ResponseEntity<>(makeMap("error", "your ship are overlapped"), HttpStatus.FORBIDDEN);
              } else{
                  Ship.forEach ship-> gamePlayer.addShip(ship);
                  gamePlayerRepository.save(gamePlayer);
                  response = new ResponseEntity<>(makeMap("success", "ships added"), HttpStatus.CREATED);
              }
          }

      }
            return response;
     }

     private Map<String, Object> makeMap(String key, Object value){
         Map<String, Object> map = new HashMap<>();
         map.put(key, value);
         return map;
     }

     private boolean isGuest (Authentication authentication){
         return authentication == null || authentication instanceof AnonymousAuthenticationToken;
     }

     private boolean isOutOfRange (Ship ship){
         for (String cell: ship.getlocations()){
             if (! (cell instanceof String) || cell.length()<2){
                 return true;
             } char y = cell.substring(0;1).charAt(0);
             Interger x;
             try {
                 x= Integer.parseInt(cell, substring(1));

             } catch (NumberFormatException e){
                 x=99;
             } if (x<1 || x>10 || y < 'A' || y > 'J'){
                 return true;
             }
         }
         return false;
     }

     private boolean isNotConsecutive (Ship ship){
         List<String> cells = ship.getlocation();
         boolean isVertical = cells.get(0).charAt(0)!= cells.get(1).charAt(0);
         for (int i= 0; i<cells.size(); i ++){
             if (i< cells.size()-1){
                 if (isVertical){
                     char yChar = cells.get(i).substring(0,1).charAt(0);
                     char comparechar = cells.get(i+1).substring(0,1).charAt(0);
                     if ( char comparechar - yChar !-1){
                         return true;
                     }
                 } else{
                     Integer xInt = Integer.parseInt(cells.get(i).substring(1));
                     Integer compareInt = Integer.parseInt(cells.get(i+1).substring(1));
                     if (compareInt - xInt !=1){
                         return true;
                     }
                 }
             }
         } for (int j= i+1; j<cells.size(); j++){
             if (isVertical){
                 if (!cells.get(i).substring(1).equals(cells.get(j).substring(1))){
                     return true;
                 }
             } else{
                 if(!cells.get(i).substring(0;1).equals(cells.get(j).substring(0;1))){
                     return  true;
                 }
             }
         }
               return false;
     }

     private boolean areOverlapped (List<Ship> ships){
         List <String> allCells = new ArrayList<>();
         ships.forEach(ship -> allCells.addAll(ship.getlocations()));
         for(int i= 0; i <allCells.size(); i ++){
             for(int j= i + 1; j<allCells.size(); j ++){
                 if (allCells.get(i).equals(allCells.get(j))){
                     return true;
                 }
             }
         }

         return false;
     }
}