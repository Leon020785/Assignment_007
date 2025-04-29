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

    public void leaveGame(Long gameId, String username) {
        Game game = gameRepository.findByUid(gameId);
        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        User user = (User) userRepository.findByName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!game.getPlayers().contains(user)) {
            throw new RuntimeException("User is not in this game");
        }

        if (game.getState() != GameState.WAITING_FOR_PLAYERS) {
            throw new RuntimeException("Cannot leave a game that has already started");
        }

        game.getPlayers().remove(user);
        gameRepository.save(game);

        // Remove player entity if using separate Player table
        //playerRepository.deleteByGameAndUser(game, user);
    }



    @Transactional
    public Game createGame(Game game) {
        gameRepository.save(game);

        User owner = game.getOwner();
        if (owner != null) {
            Player player = new Player();
            player.setGame(game);
            player.setUser(owner);
            player.setName(owner.getName());
            playerRepository.save(player);
        }

        return gameRepository.findByUid(game.getUid());
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
}
