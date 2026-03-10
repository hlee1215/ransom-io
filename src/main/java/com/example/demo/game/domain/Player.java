package com.example.demo.game.domain;

import lombok.Getter;

public class Player {
    @Getter
    private String id;
    @Getter
    private String name;

    public Player(String playerId, String name) {
        this.id = playerId;
        this.name = name;
    }

}
