package com.salvo.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDate date = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> shipSet = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoSet =new HashSet<>();


    public GamePlayer (){}

    public long getId() {
        return id;
    }

    public GamePlayer (Player player, Game game){

        this.player = player;
        this.game = game;
    }

    public void addShip(Ship ship){
        ship.setGamePlayer(this);
        shipSet.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvoSet.add(salvo);
        salvo.setGamePlayer(this);
    }

    public Set<Salvo>getSalvoes(){
        return this.salvoSet;
    }

    public Set<Ship> getShipSet() {
        return shipSet;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Map<String, Object> getGamePlayerData() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gamePlayerId", getId());
        dto.put("player", getPlayer().getPlayerData());

        Score score = getPlayer().getGetScoreByGame(this.getGame());
        if(score !=null){
            dto.put("score", score.getPoints());
        } else {
            dto.put("score", null);
        }


        return dto;
    }

}

