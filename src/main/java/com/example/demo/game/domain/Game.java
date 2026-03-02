package com.example.demo.game.domain;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Game {

    private final String id;
    private final String hostId;
    private int roundNumber;
    private String currentPrompt;

    private GameState state;

    private final Map<String, Player> players = new HashMap<>(); // add a player state later
    private final Map<String, String> submissions = new HashMap<>();
    private final Map<String, Integer> scores = new HashMap<>();
    private int maxPlayerCount = 5; // Hardcoded for now


    private Instant roundEndTime;

    public Game(String id, String hostId) {
        this.id = id;
        this.hostId = hostId;
        this.state = GameState.LOBBY;
        this.roundNumber = 0;
    }

    public void startGame() {
        if (state != GameState.LOBBY) {
            throw new IllegalStateException("Game already started");
        }

        if (players.size() < 2) {
            throw new IllegalStateException("Not enough players to start");
        }

        // initialize round data
        state = GameState.SUBMISSION;
        roundNumber = 1;
        roundEndTime = Instant.now().plusSeconds(60);
    }
    public void addPlayer(Player player) {

        if (state != GameState.LOBBY) {
            throw new IllegalStateException("Cannot join game after it has started");
        }

        String playerId = player.getId();

        if (players.containsKey(playerId)) {
            throw new IllegalStateException("Player already joined");
        }

        if (players.size() >= maxPlayerCount) {
            throw new IllegalStateException("Maximum player count reached");
        }

        players.put(playerId, player);
        scores.put(playerId, 0);
    }

    public void submit(String playerId, String text) {
        if (state != GameState.SUBMISSION) {
            throw new IllegalStateException("Submissions are not allowed right now");
        }

        if (!players.containsKey(playerId)) {
            throw new IllegalStateException("Player not in game");
        }

        if (roundEndTime == null || Instant.now().isAfter(roundEndTime)) {
            throw new IllegalStateException("Round has ended");
        }

        if (submissions.containsKey(playerId)) {
            throw new IllegalStateException("Player already submitted this round");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Submission cannot be blank");
        }

        submissions.put(playerId, text);
    }

    public void endRound() {
        if (state != GameState.SUBMISSION) {
            throw new IllegalStateException("Cannot end round in current state");
        }

        state = GameState.SCORING;
    }

    public void submit() {
        if (state != GameState.SUBMISSION) {
            throw new IllegalStateException("Can't submit in current state");
        }

    }

}

