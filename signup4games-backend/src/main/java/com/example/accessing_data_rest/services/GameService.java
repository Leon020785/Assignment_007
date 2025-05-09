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

import java.util.ArrayList;
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
        if (game.getOwner() == null || game.getOwner().getName() == null) {
            throw new IllegalArgumentException("Game must have a valid owner.");
        }

        if (game.getName() == null || game.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be empty.");
        }

        game.setState(GameState.SIGNUP);
        gameRepository.save(game);
        System.out.println("✅ Game created: " + game.getUid());

        // Automatically add the owner as a player
        Player player = new Player();
        player.setGame(game);
        player.setUser(game.getOwner());
        player.setName(game.getOwner().getName());
        playerRepository.save(player);

        System.out.println("👤 Player created for owner: " + game.getOwner().getName());

        return game;
    }
    @Transactional
    public Player joinGame(Game game, User user) {
        Player player = new Player();
        player.setGame(game);
        player.setUser(user);
        player.setName(user.getName());

        playerRepository.save(player);
        return player;
    }
    public GameRepository getGameRepository() {
        return gameRepository;
    }
    public List<Game> getGames() {
        List<Game> result = new ArrayList<>();
        gameRepository.findAll().forEach(result::add);
        return result;
    }
    public List<Game> getOpenGames() {
        List<Game> result = new ArrayList<>();
        gameRepository.findByStateIs(GameState.SIGNUP).forEach(result::add);
        return result;
    }

    public List<Game> getGamesByName(String name) {
        return gameRepository.findByName(name);
    }
    @Transactional
    public Game startGame(Game game) {
        game.setState(GameState.ACTIVE);
        return gameRepository.save(game);
    }
    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public void deleteGameIfOwner(long gameId, String username) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        User owner = game.getOwner();
        if (owner == null || !owner.getName().equals(username)) {
            throw new RuntimeException("Only the game owner can delete this game");
        }

        // Remove all players from the game before deletion
        playerRepository.deleteAll(game.getPlayers());
        gameRepository.delete(game);

        System.out.println("✅ Game with ID " + gameId + " deleted successfully.");
    }

    public Game startGame(long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (game.getState() != GameState.SIGNUP) {
            throw new IllegalStateException("Game is not in SIGNUP state");
        }

        long playerCount = gameRepository.countByStateAndUid(GameState.SIGNUP, gameId);
        if (playerCount < game.getMinPlayers()) {
            throw new IllegalStateException("Not enough players to start the game");
        }

        game.setState(GameState.ACTIVE);
        return gameRepository.save(game);
    }

}
