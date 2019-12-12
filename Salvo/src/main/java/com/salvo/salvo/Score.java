package com.salvo.salvo;

import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name="native", strategy ="native")
    private long id;

    private LocalDate finishdate = LocalDate.now();

    private int points;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    public Score(){}

    public Score(int points, Game game, Player player){
        this.points= points;
        this.game= game;
        this.player= player;
    }

    public long getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
