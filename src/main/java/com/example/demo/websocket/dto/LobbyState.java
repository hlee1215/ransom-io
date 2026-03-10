

package com.example.demo.websocket.dto;

import java.util.List;

public record LobbyState(
        String gameId,
        String state,
        int playerCount,
        List<PlayerView> players
) {}
