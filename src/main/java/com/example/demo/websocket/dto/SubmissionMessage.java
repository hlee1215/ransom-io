package com.example.demo.websocket.dto;

public record SubmissionMessage(
        String gameId,
        String playerId,
        String submission
) {
}
