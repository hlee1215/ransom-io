package com.example.demo.websocket.dto;

import java.util.List;
import java.util.Map;

public record ScoringState(
        String gameId,
        int roundNumber,
        List<SubmissionView> submissions,
        Map<String, Integer> scores
) {}
