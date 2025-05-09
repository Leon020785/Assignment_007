package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.PlayerRequest;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import com.example.accessing_data_rest.services.GameService;
import com.example.accessing_data_rest.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/roborally/players")
public class PlayerController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public Player createPlayer(@RequestBody PlayerRequest playerRequest) throws URISyntaxException {
        URI userUri = new URI(playerRequest.getUser());
        String[] segments = userUri.getPath().split("/");
        Long userId = Long.parseLong(segments[segments.length - 1]);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        Player player = new Player();
        player.setUser(user);

        return playerRepository.save(player);
    }

    @DeleteMapping("/leave")
    public ResponseEntity<String> leaveGame(@RequestParam long playerId, @RequestParam String username) {
        try {
            playerService.deletePlayer(playerId, username);
            return ResponseEntity.ok("Player successfully removed from game");
        } catch (Throwable e) {
            return ResponseEntity.status(403).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/start")
    public Game startGame(@PathVariable("id") long gameId) {
        return gameService.startGame(gameId);
    }


}
