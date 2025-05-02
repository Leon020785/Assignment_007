package com.example.accessing_data_rest;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return gameService.getOpenGames();
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Game postGame(@RequestBody Game game) {
        return gameService.createGame(game);
    }

    @PostMapping(value = "/joingame", consumes = "application/json", produces = "application/json")
    public Player joinGame(@RequestParam("gameid") long gameid, @RequestBody User user) {
        Game game = gameService.getGameRepository().findPlayerByUid(gameid);
        return gameService.joinGame(game, user);
    }

    @PostMapping(value = "/startgame", consumes = "application/json", produces = "application/json")
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
