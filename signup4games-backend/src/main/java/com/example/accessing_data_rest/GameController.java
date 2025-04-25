package com.example.accessing_data_rest;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.services.GameService;


import com.example.accessing_data_rest.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roborally/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // Endpoint for at hente alle spil
    @GetMapping(value = "", produces = "application/json")
    public List<Game> getGames() {
        return gameService.getGames();
    }

    // Endpoint for at hente alle åbne spil (state = SIGNUP)
    @GetMapping(value = "/opengames", produces = "application/json")
    public List<Game> getOpenGames() {
        return gameService.getOpenGames();
    }
    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Game postGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }
    @PostMapping(value = "/joingame", consumes = "application/json", produces = "application/json")
    public Player joinGame(@RequestParam("gameid") long gameid, @RequestBody User user) {
        Game game = gameService.getGameRepository().findByUid(gameid);
        return gameService.joinGame(game, user);
    }
    @PostMapping(value = "/startgame", consumes = "application/json", produces = "application/json")
    public Game startGame(@RequestBody Game game) {
        return gameService.startGame(game);
    }
    @DeleteMapping("/player/{id}")
    public void deletePlayer(@PathVariable("id") long id) {
        gameService.getPlayerRepository().deleteById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable("id") long id) {
        gameService.getGameRepository().deleteById(id);
    }








}
