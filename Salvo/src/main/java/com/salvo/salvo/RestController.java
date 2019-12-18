package com.salvo.salvo;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


    @RequestMapping(path = "/api/games", method = RequestMethod.GET)
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "guest");
        } else {
            dto.put("player", playerRepository.findPlayerByUserName(authentication.getName()).getPlayerData());
        }
        dto.put("games", gameRepository.findAll().stream().map(Game::getGameData).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path = "/api/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity response;
        if (isGuest(authentication)) {
            response = new ResponseEntity(makeMap("error", "You must logged in first"), HttpStatus.UNAUTHORIZED);
        } else {
            Player player = playerRepository.findPlayerByUserName(authentication.getName());
            System.out.println(player.getEmail());
            Game newGame = new Game();
            gameRepository.save(newGame);
            GamePlayer newGamePlayer = new GamePlayer(player, newGame);
            gamePlayerRepository.save(newGamePlayer);
            response = new ResponseEntity(makeMap("gpId", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    private Object makeMap(String error, String you_must_logged_in_first) {
        return null;
    }


    @RequestMapping("/api/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId) {
        return gamePlayerRepository.findById(gamePlayerId).map(this::gameViewDto).orElse(null);
    }

    public Map<String, Object> gameViewDto(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if (gamePlayer != null) {
            dto.put("id", gamePlayer.getGame().getId());
            dto.put("creationDate", gamePlayer.getGame().getDate());
            dto.put("gamePlayer", gamePlayer.getGame().getGamePlayerSet().stream().map(GamePlayer::getGamePlayerData));
            dto.put("ship", gamePlayer.getShipSet().stream().map(Ship::getShipData));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayerSet().stream().flatMap(gp -> gp.getSalvoes().stream().map(Salvo::salvoDto)));
        } else {
            dto.put("error", "no such game");

        }

        return dto;

    }

    //registrar players
    @RequestMapping(path = "/api/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(@RequestParam String username, @RequestParam String password) {

        Map<String, Object> respuesta = new HashMap<>();
        if (username.isEmpty() || password.isEmpty()) {
            respuesta.put("error", "Missing data");
            return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findPlayerByUserName(username) != null) {
            respuesta.put("error", "Name already in use");
            return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // task 3 //

    @RequestMapping(path = "/api/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable("gamePlayerId") Long idGamePlayer, @RequestBody List<Ship> ships) {
        ResponseEntity response;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isGuest(authentication)) {

            response = new ResponseEntity(makeMap("error", "you must be loggedin"), HttpStatus.UNAUTHORIZED);
        } else {
            System.out.println("Ingrese");
            GamePlayer gamePlayer = gamePlayerRepository.findById(idGamePlayer).orElse(null);
            Player player = playerRepository.findPlayerByUserName(authentication.getName());
            if (gamePlayer == null) {
                System.out.println("No Such Game");
                response = new ResponseEntity(makeMap("error", "no such game"), HttpStatus.FORBIDDEN);
            } else if (gamePlayer.getPlayer().getId() != player.getId()) {
                System.out.println("this is not your game");
                response = new ResponseEntity(makeMap("error", "this is not your game"), HttpStatus.UNAUTHORIZED);
            } else if (gamePlayer.getShipSet().size() > 0) {
                System.out.println("you already have ships");
                response = new ResponseEntity(makeMap("error", "you already have ships"), HttpStatus.FORBIDDEN);
            } else if (ships == null || ships.size() != 5) {
                System.out.println("you must add 5 ships");
                response = new ResponseEntity(makeMap("error", "you must add 5 ships"), HttpStatus.FORBIDDEN);
            } else {
                if (!ships.stream().anyMatch(this::isOutOfRange)) {
                    System.out.println("you have ships out of range");
                    response = new ResponseEntity(makeMap("error", "you have ships out of range"), HttpStatus.FORBIDDEN);
                } else if (!ships.stream().anyMatch(this::isNotConsecutive)) {
                    System.out.println("your ships are not consecutive");
                    response = new ResponseEntity(makeMap("error", "your ships are not consecutive"), HttpStatus.FORBIDDEN);
                } else if (this.areOverlapped(ships)) {
                    System.out.println("your ship are overlapped");
                    response = new ResponseEntity(makeMap("error", "your ship are overlapped"), HttpStatus.FORBIDDEN);
                } else {
                    System.out.println("ships added");
                    ships.forEach(gamePlayer::addShip);
                    gamePlayerRepository.save(gamePlayer);
                    response = new ResponseEntity(makeMap("success", "ships added"), HttpStatus.CREATED);
                }
            }

        }
        return response;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private boolean isOutOfRange(Ship ship) {
        for (String cell : ship.getLocations()) {
            char y = cell.substring(0, 1).charAt(0);
            int x;
            try {
                x = Integer.parseInt(cell, 1);
            } catch (NumberFormatException e) {
                x = 99;
            }
            if (x < 1 || x > 10 || y < 'A' || y > 'J') {
                return true;
            }
        }
        return false;
    }

    private boolean isNotConsecutive(Ship ship) {
        List<String> cells = ship.getLocations();
        boolean isVertical = cells.get(0).charAt(0) != cells.get(1).charAt(0);
        for (int i = 0; i < cells.size(); i++) {
            if (i < cells.size() - 1) {
                if (isVertical) {
                    char yChar = cells.get(i).substring(0, 1).charAt(0);
                    char comparechar = cells.get(i + 1).substring(0, 1).charAt(0);
                    if (comparechar - yChar != -1) {
                        return true;
                    }
                } else {
                    Integer xInt = Integer.parseInt(cells.get(i).substring(1));
                    Integer compareInt = Integer.parseInt(cells.get(i + 1).substring(1));
                    if (compareInt - xInt != 1) {
                        return true;
                    }
                }
            }
            for (int j = i + 1; j < cells.size(); j++) {
                if (isVertical) {
                    if (!cells.get(i).substring(1).equals(cells.get(j).substring(1))) {
                        return true;
                    }
                } else {
                    if (!cells.get(i).substring(0, 1).equals(cells.get(j).substring(0, 1))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean areOverlapped(List<Ship> ships) {
        List<String> allCells = new ArrayList<>();
        ships.forEach(ship -> allCells.addAll(ship.getLocations()));
        for (int i = 0; i < allCells.size(); i++) {
            for (int j = i + 1; j < allCells.size(); j++) {
                if (allCells.get(i).equals(allCells.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }


    @RequestMapping(path = "/api/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvo(@PathVariable Long gamePlayerId, @RequestBody List<String> shots) {
        ResponseEntity<Map<String, Object>> response;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(shots);
        if (isGuest(authentication)) {
            response = new ResponseEntity(makeMap("error", "you must be lloggedin"), HttpStatus.UNAUTHORIZED);

        } else {
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findPlayerByUserName(authentication.getName());
            if (gamePlayer == null) {
                response = new ResponseEntity(makeMap("error", "no such game"), HttpStatus.NOT_FOUND);
            } else if (gamePlayer.getPlayer().getId() != player.getId()) {
                response = new ResponseEntity(makeMap("error", "this is not your game"), HttpStatus.UNAUTHORIZED);
            } else if (shots.size() != 5) {
                response = new ResponseEntity(makeMap("error", "worng number of shots"), HttpStatus.FORBIDDEN);
            } else {
                int turn = gamePlayer.getSalvoes().size() + 1;

                Salvo salvo = new Salvo(turn, shots);
                gamePlayer.addSalvo(salvo);

                gamePlayerRepository.save(gamePlayer);

                response = new ResponseEntity(makeMap("success", "salvo added"), HttpStatus.OK);
            }
        }
        return response;
    }
}
