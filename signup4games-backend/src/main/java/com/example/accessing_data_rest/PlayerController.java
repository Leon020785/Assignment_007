package com.example.accessing_data_rest;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.PlayerRequest;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.PlayerRepository;
import com.example.accessing_data_rest.repositories.UserRepository;
import com.example.accessing_data_rest.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/roborally/players")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Player createPlayer(@RequestBody PlayerRequest playerRequest) throws URISyntaxException {
        // Eksempel på input i playerRequest.getUser():
        // "http://localhost:8080/roborally/users/4"

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

    @PostMapping("/{id}/start")
    public Game startGame(@PathVariable("id") long gameId) {
        return GameService.startGame(gameId);
    }
}
