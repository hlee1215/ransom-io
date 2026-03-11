package com.example.demo.game.manager;

import com.example.demo.game.domain.Game;
import com.example.demo.game.domain.Player;
import com.example.demo.websocket.dto.LobbyState;
import com.example.demo.websocket.dto.PlayerView;
import com.example.demo.websocket.dto.SubmissionState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class GameManager {

    private final Map<String, Game> games = new ConcurrentHashMap<>();

    public String createGame(String hostId, int maxRounds) {
        String gameId = UUID.randomUUID().toString();
        Game game = new Game(gameId, hostId);
        game.setMaxRounds(maxRounds);
        games.put(gameId, game);
        return gameId;
    }
    //Helper, if read only data is needed, return DTO instead
    private Game getGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) throw new NoSuchElementException("Game not found: " + gameId);
        return game;
    }

    public void addPlayer(String gameId, Player player) {
        Game game = getGame(gameId);
        synchronized (game) {
            game.addPlayer(player);
        }
    }

    public void startGame(String gameId, String requesterId) {
        Game game = getGame(gameId);
        synchronized (game) {
            game.startGame(requesterId);
        }
    }

    public void submit(String gameId, String playerId, String text) {
        Game game = getGame(gameId);
        synchronized (game) {
            game.submit(playerId, text);
        }
    }

    public void endRound(String gameId) {
        Game game = getGame(gameId);
        synchronized (game) {
            game.endRound();
        }
    }

    public void scoreRound(String gameId) {
        Game game = getGame(gameId);
        synchronized (game) {
            game.scoreRound();
        }
    }

    public void startNextRound(String gameId) {
        Game game = getGame(gameId);
        synchronized (game) {
            game.startNextRound();
        }
    }

    public LobbyState getLobbyState(String gameId) {
        Game game = getGame(gameId);

        synchronized (game) {

            List<PlayerView> players = game.getPlayers()
                    .values()
                    .stream()
                    .map(p -> new PlayerView(p.getId(), p.getName()))
                    .toList();

            return new LobbyState(
                    gameId,
                    game.getState().name(),
                    players.size(),
                    players
            );
        }
    }

    public SubmissionState getSubmissionState (String gameId){
        Game game = getGame(gameId);
        synchronized (game){
            return new SubmissionState(
                    gameId,
                    game.getRoundNumber(),
                    game.getRoundEndTime(),
                    game.getSubmissions().size(),
                    game.getPlayers().size()
            );
        }
    }
}