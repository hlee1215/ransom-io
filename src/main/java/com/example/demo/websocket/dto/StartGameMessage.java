package com.example.demo.websocket.dto;

public record StartGameMessage(
        String gameId,
        String playerId
) {}