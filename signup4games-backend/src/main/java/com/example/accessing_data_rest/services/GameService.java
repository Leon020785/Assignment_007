package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.GameRepository;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public Game createGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }

        if (game.getOwner() == null || game.getOwner().getName() == null || game.getOwner().getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Game must have a valid owner with a non-empty name.");
        }

        if (game.getName() == null || game.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be empty.");
        }

        // Set the initial state
        game.setState(GameState.SIGNUP);
        Game savedGame = gameRepository.save(game);
        System.out.println("✅ Game created with ID: " + savedGame.getUid() + " and name: " + savedGame.getName());

        // Automatically add the owner as a player
        Player ownerPlayer = new Player();
        ownerPlayer.setGame(savedGame);
        ownerPlayer.setUser(savedGame.getOwner());
        ownerPlayer.setName(savedGame.getOwner().getName());
        playerRepository.save(ownerPlayer);

        System.out.println("👤 Player created for owner: " + savedGame.getOwner().getName() + " in game: " + savedGame.getUid());

        return savedGame;
    }

    @Transactional
    public Player joinGame(Game game, User user) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }

        if (user == null || user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User must have a valid name.");
        }

        if (!"SIGNUP".equalsIgnoreCase(String.valueOf(game.getState()))) {
            throw new IllegalStateException("Cannot join a game that is not in SIGNUP state. Current state: " + game.getState());
        }

        // Check if the user is already a player in this game
        if (game.getPlayers().stream().anyMatch(player -> player.getUser().getUid() == user.getUid())) {
            throw new IllegalStateException("User " + user.getName() + " is already a player in this game.");
        }

        Player player = new Player();
        player.setGame(game);
        player.setUser(user);
        player.setName(user.getName());
        playerRepository.save(player);

        System.out.println("✅ User " + user.getName() + " joined game " + game.getUid());

        return player;
    }

    @Transactional
    public Game startGame(long gameId) {
        System.out.println("🚀 Attempting to start game with ID: " + gameId);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game with ID " + gameId + " not found."));

        if (!"SIGNUP".equalsIgnoreCase(String.valueOf(game.getState()))) {
            throw new IllegalStateException("Game is not in SIGNUP state. Current state: " + game.getState());
        }

        int playerCount = game.getPlayers().size();
        if (playerCount < game.getMinPlayers()) {
            throw new IllegalStateException("Not enough players to start the game. Current count: " + playerCount + ", required: " + game.getMinPlayers());
        }

        // Transition to ACTIVE state
        game.setState(GameState.ACTIVE);
        gameRepository.save(game);

        System.out.println("✅ Game with ID " + gameId + " started successfully with " + playerCount + " players.");

        return game;
    }

    public List<Game> getGames() {
        System.out.println("📋 Fetching all games...");
        List<Game> games = new java.util.ArrayList<>();
        gameRepository.findAll().forEach(games::add);
        System.out.println("✅ Found " + games.size() + " games.");
        return games;
    }

    public List<Game> getOpenGames() {
        System.out.println("📋 Fetching all open games in SIGNUP state...");
        List<Game> openGames = gameRepository.findByStateIs(GameState.SIGNUP);

        if (openGames.isEmpty()) {
            System.out.println("⚠️ No open games found in SIGNUP state.");
        } else {
            System.out.println("✅ Found " + openGames.size() + " open games in SIGNUP state.");
        }

        return openGames;
    }

    public List<Game> getGamesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be empty.");
        }

        System.out.println("🔍 Searching for games with name: " + name);
        List<Game> games = gameRepository.findByName(name);

        if (games.isEmpty()) {
            System.out.println("⚠️ No games found with the name: " + name);
        } else {
            System.out.println("✅ Found " + games.size() + " game(s) with the name: " + name);
        }

        return games;
    }

    @Transactional
    public void deleteGameIfOwner(long gameId, String username) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        User owner = game.getOwner();
        if (owner == null || !owner.getName().equals(username)) {
            throw new RuntimeException("Only the game owner can delete this game.");
        }

        // Remove all players before deleting the game to avoid orphan records
        playerRepository.deleteAll(game.getPlayers());
        gameRepository.delete(game);

        System.out.println("✅ Game with ID " + gameId + " deleted successfully.");
    }

    public GameRepository getGameRepository() {
        return gameRepository;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }
}
