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

    public void deletePlayer(long playerId, String username) throws Throwable {
        System.out.println("Attempting to delete player with ID: " + playerId);

        Player player = playerRepository.findByUid(playerId);
                if (player == null) {
                    throw new Exception("Player with ID: " + playerId + " not found");
                }

        Game game = player.getGame();
        User user = player.getUser();

        // Kun spilleren selv eller spillets ejer må slette spilleren
        if (!user.getName().equals(username) &&
                !game.getOwner().getName().equals(username)) {
            throw new RuntimeException("Not authorized to delete this player");
        }

        game.getPlayers().remove(player); // fjern fra spillets liste
        playerRepository.delete(player);  // slet fra databasen
        gameRepository.save(game);        // gem spillet igen
    }



    @Transactional
    public Game createGame(Game game) {
        gameRepository.save(game);

        System.out.println("✅ Game created: " + game.getUid());



        User owner = game.getOwner();
        if (owner != null) {
            Player player = new Player();
            player.setGame(game);
            player.setUser(owner);
            player.setName(owner.getName());
            playerRepository.save(player);

            System.out.println("👤 Player created: " + owner.getName());

        } else {
            System.out.println("No owner set for the game. ");
        }

        return gameRepository.findPlayerByUid(game.getUid());
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
