package com.example.demo.websocket;

import com.example.demo.game.manager.GameManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final GameManager gameManager;

    public DebugController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @GetMapping("/create")
    public String createGame() {
        return gameManager.createGame("p1", 5);
    }

    @DeleteMapping("/{gameId}")
    public void deleteGame(@PathVariable String gameId) {
        gameManager.removeGame(gameId);
    }
}
