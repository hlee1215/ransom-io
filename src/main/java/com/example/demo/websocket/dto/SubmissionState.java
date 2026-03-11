package com.example.demo.websocket.dto;

import java.time.Instant;

public record SubmissionState(
        String gameId,
        int roundNumber,
        Instant roundEndTime,
        int submittedCount,
        int totalPlayers
) {}