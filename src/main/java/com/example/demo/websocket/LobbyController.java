

package com.example.demo.websocket;

import com.example.demo.game.domain.Player;
import com.example.demo.game.manager.GameManager;
import com.example.demo.websocket.dto.JoinLobbyMessage;
import com.example.demo.websocket.dto.LobbyState;
import com.example.demo.websocket.dto.StartGameMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyController {

    private final GameManager gameManager;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyController(GameManager gameManager, SimpMessagingTemplate messagingTemplate) {
        this.gameManager = gameManager;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/lobby.join")
    public void joinLobby(JoinLobbyMessage message) {
        LobbyState lobbyState = gameManager.getLobbyState(message.gameId());
        Player player = new Player(
                message.playerId(),
                message.name()
        );

        gameManager.addPlayer(
                message.gameId(),
                player
        );
        messagingTemplate.convertAndSend(
                "/topic/lobby." + message.gameId(),
                lobbyState
        );
    }

    @MessageMapping("/game.start")
    public void startGame(StartGameMessage message) {
        gameManager.startGame(
                message.gameId(), message.playerId()
        );

        LobbyState state = gameManager.getLobbyState(message.gameId());

        messagingTemplate.convertAndSend(
                "/topic/lobby." + message.gameId(),
                state
        );
    }

}