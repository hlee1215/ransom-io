package com.example.demo.websocket.dto;

public record VoteMessage(
        String gameId,
        String playerId,
        String submissionId
) {}
