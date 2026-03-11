package com.example.demo.websocket.dto;

import java.util.Map;

public record ScoringState(
        String gameId,
        int roundNumber,
        Map<String, String> submissions,
        Map<String, Integer> scores
) {}
