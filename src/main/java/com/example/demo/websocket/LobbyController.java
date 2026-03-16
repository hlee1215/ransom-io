

package com.example.demo.websocket;

import com.example.demo.game.domain.Player;
import com.example.demo.game.manager.GameManager;
import com.example.demo.game.timer.RoundTimerService;
import com.example.demo.websocket.dto.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class LobbyController {

    private final GameManager gameManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoundTimerService roundTimerService;

    public LobbyController(GameManager gameManager, SimpMessagingTemplate messagingTemplate, RoundTimerService roundTimerService) {
        this.gameManager = gameManager;
        this.messagingTemplate = messagingTemplate;
        this.roundTimerService = roundTimerService;
    }

    @MessageMapping("/lobby.join")
    public void joinLobby(JoinLobbyMessage message) {

        Player player = new Player(
                message.playerId(),
                message.name()
        );

        gameManager.addPlayer(
                message.gameId(),
                player
        );

        LobbyState lobbyState = gameManager.getLobbyState(message.gameId());

        messagingTemplate.convertAndSend(
                "/topic/lobby." + message.gameId(),
                lobbyState
        );
    }

    @MessageMapping("/game.start")
    public void startGame(StartGameMessage message) {
        gameManager.startGame(
                message.gameId(),
                message.playerId() //ensures only host can start
        );

        Instant roundEndTime = gameManager.getRoundEndTime(message.gameId());
        roundTimerService.scheduleRoundEnd(message.gameId(), roundEndTime);

        SubmissionState state = gameManager.getSubmissionState(message.gameId());

        messagingTemplate.convertAndSend(
                "/topic/game." + message.gameId(),
                state
        );
    }

    @MessageMapping("/game.submit")
    public void submit(SubmissionMessage message) {
        gameManager.submit(
                message.gameId(),
                message.playerId(),
                message.submission()
        );

        if (gameManager.allPlayersSubmitted(message.gameId())) {
            roundTimerService.cancelRoundTimer(message.gameId());

            gameManager.endRound(message.gameId());

            ScoringState scoringState = gameManager.getScoringState(message.gameId());

            messagingTemplate.convertAndSend(
                    "/topic/game." + message.gameId(),
                    scoringState
            );
        } else {
            SubmissionState submissionState = gameManager.getSubmissionState(message.gameId());

            messagingTemplate.convertAndSend(
                    "/topic/game." + message.gameId(),
                    submissionState
            );
        }
    }

    @MessageMapping("/game.vote")
    public void vote(VoteMessage message) {

        boolean roundFinished = gameManager.vote(
                message.gameId(),
                message.playerId(),
                message.submissionId()
        );

        if (roundFinished) {

            gameManager.startNextRound(message.gameId());
            if (gameManager.isGameFinished(message.gameId())) {
                gameManager.removeGame(message.gameId());
            }
            Instant roundEndTime =
                    gameManager.getRoundEndTime(message.gameId());

            roundTimerService.scheduleRoundEnd(
                    message.gameId(),
                    roundEndTime
            );

            //obtain different snapshot
            SubmissionState state =
                    gameManager.getSubmissionState(message.gameId());

            messagingTemplate.convertAndSend(
                    "/topic/game." + message.gameId(),
                    state
            );

        } else {

            ScoringState state =
                    gameManager.getScoringState(message.gameId());

            messagingTemplate.convertAndSend(
                    "/topic/game." + message.gameId(),
                    state
            );
        }
    }




}