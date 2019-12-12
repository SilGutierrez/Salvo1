package com.salvo.salvo;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity

public class Ship {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
        @GenericGenerator(name = "native", strategy = "native")
        private long id;
        private String type;


        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn (name = "gamePlayer_id")
        private GamePlayer gamePlayer;

        @ElementCollection
        @Column(name= "locations")
        private List<String> locations = new ArrayList<>();

        public Ship(){}

        public Ship(String type, List<String> locations) {
            this.type = type;
            this.locations = locations;
        }

        public long getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public GamePlayer getGamePlayer() {
            return gamePlayer;
        }

        public void setGamePlayer(GamePlayer gameplayer) {
            this.gamePlayer = gameplayer;
        }

        public Map<String, Object> getShipData(){
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("type", type);
            dto.put("locations", locations);
            return dto;

        }


}

