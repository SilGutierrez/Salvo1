package com.salvo.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.*;

@Entity

public class Player {
    //atributos
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;
    private String passWord;
    private String firstName;
    private String lastName;
    private String email;

    @OneToMany (mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<GamePlayer> gamePlayerSet = new HashSet<>();


    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scoreSet = new HashSet<>();

    //constructor
    public Player(){}

    public Player(String userName, String passWord){
        this.userName = userName;
        this.passWord = passWord;
    }

    public Player(String userName, String firstName, String lastName, String email, String passWord) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passWord = passWord;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Map<String, Object> getPlayerData() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("playerId", getId());
        dto.put("userName", getUserName());
        dto.put("email", getEmail());
        return dto;
    }

    //getter y setter


    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() { return email; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setEmail(String email) { this.email = email; }

    public Score getGetScoreByGame(Game game){
        Score score;
        score = scoreSet.stream().filter(e -> (e.getGame().getId() == game.getId())).findFirst().orElse(null);
        return score;
    }

}