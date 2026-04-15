package com.example.demo.game.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

public class Game {
    private static final long ROUND_DURATION_SECONDS = 60;
    @Getter
    private final String id;
    @Getter
    private final String hostId;
    @Getter
    private int roundNumber;
    @Getter
    private String currentPrompt;
    @Setter
    private int maxRounds = 5;

    @Getter
    private GameState state;
    @Getter
    private final Map<String, Player> players = new HashMap<>();
     // add a player state later
    private final Map<String, String> submissions = new HashMap<>();
    @Getter
    private final Map<String, Integer> scores = new HashMap<>();
    private final Map<String, String> submissionIdToPlayer = new HashMap<>();
    private int maxPlayerCount = 5; // Hardcoded for now

    public Map<String, Player> getPlayers() {
        return Collections.unmodifiableMap(players);
    }
    public Map<String, String> getSubmissions() {
        return Collections.unmodifiableMap(submissions);
    }
    public Map<String, String> getSubmissionIdToPlayer() { return Collections.unmodifiableMap(submissionIdToPlayer);}

    @Getter
    private Instant roundEndTime;
    public Game(String id, String hostId) {
        this.id = id;
        this.hostId = hostId;
        this.state = GameState.LOBBY;
        this.roundNumber = 0;
    }
    //-----------------------------------------LOBBY--------------------------------------------------------//
    public void startGame(String requesterId) {
        if (!hostId.equals(requesterId)) {
            throw new IllegalStateException("Only host can start game");
        }

        if (state != GameState.LOBBY) {
            throw new IllegalStateException("Game already started");
        }

        if (players.size() < 2) {
            throw new IllegalStateException("Not enough players to start");
        }

        // initialize round data
        state = GameState.SUBMISSION;
        roundNumber = 1;
        currentPrompt = "Current Prompt";
        roundEndTime = Instant.now().plusSeconds(ROUND_DURATION_SECONDS);
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

    //-----------------------------------------SUBMISSION--------------------------------------------------------//
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

    public Map<String, String> buildSubmissionMapping() {

        if (!submissionIdToPlayer.isEmpty()) {
            return submissionIdToPlayer; // already built → reuse
        }

        List<Map.Entry<String, String>> entries =
                new ArrayList<>(submissions.entrySet());

        Collections.shuffle(entries);

        int id = 0;

        for (Map.Entry<String, String> entry : entries) {
            submissionIdToPlayer.put(String.valueOf(id++), entry.getKey());
        }

        return submissionIdToPlayer;
    }

    public void assignWords(){}

    public void endRound() {
        if (state != GameState.SUBMISSION) {
            return;
        }

        submissionIdToPlayer.clear();
        state = GameState.SCORING;

    }

    //-----------------------------------------SCORING--------------------------------------------------------//
    public void incrementScore(String playerId) {
        if (!players.containsKey(playerId)) {
            throw new IllegalStateException("Invalid player");
        }
        scores.put(playerId, scores.getOrDefault(playerId, 0) + 1);
    }

    public void startNextRound() {
        if (state != GameState.SCORING) {
            throw new IllegalStateException("Not in scoring state");
        }

        if (roundNumber >= maxRounds) {
            state = GameState.FINISHED;
            roundEndTime = null;
            currentPrompt = null;
            return;
        }

        roundNumber++;
        submissions.clear();
        currentPrompt = "Current Prompt"; // stub for now, promptService.generate.prompt() later
        roundEndTime = Instant.now().plusSeconds(ROUND_DURATION_SECONDS);
        state = GameState.SUBMISSION;
    }

    public boolean allPlayersSubmitted() {
        return submissions.size() == players.size();
    }




}

