package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.services.GameService;
import com.example.accessing_data_rest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private UserService userService;

    @GetMapping(value = "", produces = "application/json")
    public List<Game> getGames() {
        return gameService.getGames();
    }

    @GetMapping(value = "/open", produces = "application/json")
    public List<Game> getOpenGames() {
        return gameService.getOpenGames();
    }

    @GetMapping(value = "/search", produces = "application/json")
    public List<Game> getGamesByName(@RequestParam("name") String name) {
        return gameService.getGamesByName(name);
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Game postGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }


    @PostMapping(value = "/join", produces = "application/json")
    public Player joinGame(@RequestParam("gameId") long gameId, @RequestParam("userId") long userId) {
        Game game = gameService.getGameRepository().findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        return gameService.joinGame(game, userId);
    }



    @PostMapping(value = "/start", consumes = "application/json", produces = "application/json")
    public Game startGame(@RequestBody Game game) {
        return gameService.startGame(game);
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createEmptyGame() {
        Game game = new Game();
        game.setName("New Game");
        game.setMinPlayers(2);
        game.setMaxPlayers(6);
        game.setState(GameState.WAITING_FOR_PLAYERS);

        gameService.getGameRepository().save(game);
        return ResponseEntity.ok(game);
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable("id") long id) {
        gameService.getPlayerRepository().deleteById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable("id") long id,
            @RequestParam("username") String username) {
        gameService.deleteGameIfOwner(id, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}