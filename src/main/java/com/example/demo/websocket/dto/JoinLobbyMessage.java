
package com.example.demo.websocket.dto;

public record JoinLobbyMessage(
        String gameId,
        String playerId,
        String name
) {}
