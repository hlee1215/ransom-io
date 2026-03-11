package com.example.demo.websocket.dto;

import java.time.Instant;

public record SubmissionState(
        String gameId,
        String state,
        int roundNumber,
        Instant roundEndTime,
        int submittedCount,
        int playersRemaining
) {}