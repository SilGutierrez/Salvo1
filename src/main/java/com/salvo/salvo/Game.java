package com.salvo.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity

public class Game {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name= "native", strategy = "native")

    private long id;

    private LocalDate date = LocalDate.now();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<GamePlayer> gamePlayerSet = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Score> scoreSet = new HashSet<>();

    public Game(){}

    public long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Set<GamePlayer> getGamePlayerSet() {
        return gamePlayerSet;
    }

    public Map<String, Object> getGameData() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gameId", getId());
        dto.put("created", getDate());
        dto.put("gamePlayers", this.gamePlayerSet.stream().map(GamePlayer::getGamePlayerData));
        return dto;
    }

    public LocalDate getCreationDate() {
    return date;
    }
}
