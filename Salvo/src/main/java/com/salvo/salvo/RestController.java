package com.salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;



    @RequestMapping("/api/games")
    public List<Object> getAllGames() {
        return gameRepository.findAll().stream().map(Game::getGameData).collect(Collectors.toList());
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


}