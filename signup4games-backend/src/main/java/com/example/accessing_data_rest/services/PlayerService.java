package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.GameRepository;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    public void deletePlayer(long playerId, String username) {
        System.out.println("Attempting to delete player with ID: " + playerId);

        Player player = playerRepository.findByUid(playerId);
        if (player == null) {
            throw new RuntimeException("Player with ID: " + playerId + " not found");
        }

        Game game = player.getGame();
        User user = player.getUser();

        // Only allow the player or the game owner to delete the player
        if (!user.getName().equals(username) && !game.getOwner().getName().equals(username)) {
            throw new RuntimeException("Not authorized to delete this player");
        }

        // Remove the player from the game's player list
        game.getPlayers().remove(player);
        playerRepository.delete(player);  // Delete from the database
        gameRepository.save(game);        // Save the updated game

        System.out.println("✅ Player with ID " + playerId + " deleted successfully.");
    }
}