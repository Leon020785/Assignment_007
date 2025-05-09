package com.example.accessing_data_rest.controllers;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roborally/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping(value = "", produces = "application/json")
    public List<Game> getGames() {
        return gameService.getGames();
    }

    @GetMapping(value = "/opengames", produces = "application/json")
    public List<Game> getOpenGames() {
        Iterable<Game> allGames = gameService.getGameRepository().findAll();
        return java.util.stream.StreamSupport.stream(allGames.spliterator(), false)
                .filter(game -> {
                    String state = game.getState();
                    return "SIGNUP".equalsIgnoreCase(state) || "READY".equalsIgnoreCase(state);
                })
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/search", produces = "application/json")
    public List<Game> getGamesByName(@RequestParam("name") String name) {
        return gameService.getGamesByName(name);
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Game postGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }

    @PostMapping(value = "/joingame", consumes = "application/json", produces = "application/json")
    public Player joinGame(@RequestParam("gameid") long gameid, @RequestBody User user) {
        Game game = gameService.getGameRepository().findById(gameid)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameid));
        return gameService.joinGame(game, user);
    }

    @PostMapping(value = "/startgame", consumes = "application/json", produces = "application/json")
    public Game startGame(@RequestBody Game game) {
        Game existingGame = gameService.getGameRepository().findById(game.getUid())
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + game.getUid()));

        // Set the game to active if it has enough players
        if (existingGame.getPlayers().size() >= existingGame.getMinPlayers()) {
            existingGame.setStarted(true);
            gameService.getGameRepository().save(existingGame);
        }

        return existingGame;
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createEmptyGame() {
        Game game = new Game();
        game.setName("New Game");
        game.setMinPlayers(2);
        game.setMaxPlayers(6);
        game.setStarted(false);
        game.setFinished(false);
        game.setPlayers(new java.util.ArrayList<>());

        // Save the game and return the response
        gameService.getGameRepository().save(game);
        return ResponseEntity.ok(game);
    }


    @DeleteMapping("/player/{id}")
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





}
