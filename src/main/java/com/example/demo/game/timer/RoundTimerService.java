package com.example.demo.game.timer;

import com.example.demo.game.manager.GameManager;
import com.example.demo.websocket.dto.ScoringState;
import com.example.demo.websocket.dto.SubmissionState;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class RoundTimerService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, ScheduledFuture<?>> activeTimers = new ConcurrentHashMap<>();

    private final GameManager gameManager;
    private final SimpMessagingTemplate messagingTemplate;

    public RoundTimerService(GameManager gameManager,
                             SimpMessagingTemplate messagingTemplate) {
        this.gameManager = gameManager;
        this.messagingTemplate = messagingTemplate;
    }

    public void scheduleRoundEnd(String gameId, Instant roundEndTime) {
        cancelRoundTimer(gameId);

        long delayMillis = Math.max(0, Duration.between(Instant.now(), roundEndTime).toMillis());

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                gameManager.endRound(gameId);

                ScoringState state = gameManager.getScoringState(gameId);

                messagingTemplate.convertAndSend(
                        "/topic/game." + gameId,
                        state
                );
            } finally {
                activeTimers.remove(gameId);
            }
        }, delayMillis, TimeUnit.MILLISECONDS);

        activeTimers.put(gameId, future);
    }

    public void cancelRoundTimer(String gameId) {
        ScheduledFuture<?> existing = activeTimers.remove(gameId);
        if (existing != null) {
            existing.cancel(false);
        }
    }
}