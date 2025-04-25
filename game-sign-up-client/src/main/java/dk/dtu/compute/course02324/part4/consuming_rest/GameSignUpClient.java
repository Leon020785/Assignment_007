package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

public class GameSignUpClient extends Application {

    private RestClient client = RestClient.builder().baseUrl("http://localhost:8080").build();

    // <-- MANUELT SAT SPILLER-ID HER
    private final long playerId = 153; // Ændr til en gyldig UID fra din HAL

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        root.setSpacing(10);
        root.getChildren().add(new Label("Game Sign-Up Client"));

        // Hent spil
        List<Game> games = client.get().uri("/game").retrieve().body(HALWrapperGames.class).getGames();

        for (Game game : games) {
            TextField gameInfo = new TextField("Game: " + game.getName() + ", min: " + game.getMinPlayers() + ", max: " + game.getMaxPlayers());
            gameInfo.setEditable(false);

            Button joinButton = new Button("Join Game");
            joinButton.setOnAction(e -> {
                String gameUrl = "http://localhost:8080/game/" + game.getUid();
                ResponseEntity<Player> response = client.put()
                        .uri("/player/" + playerId + "/game")
                        .header("Content-Type", "text/uri-list")
                        .body(gameUrl)
                        .retrieve()
                        .toEntity(Player.class);
                System.out.println(response.getStatusCode().is2xxSuccessful() ? "Joined game successfully" : "Failed to join game");
            });

            Button leaveButton = new Button("Leave Game");
            leaveButton.setOnAction(e -> {
                ResponseEntity<Void> response = client.delete()
                        .uri("/player/" + playerId)
                        .retrieve()
                        .toBodilessEntity();
                System.out.println(response.getStatusCode().is2xxSuccessful() ? "Successfully left game" : "Failed to leave game");
            });
            // I din for-løkke i start() metoden, sammen med de andre knapper:
            Button startButton = new Button("Start Game");
            startButton.setOnAction(e -> {
                try {
                    // Opdater spillets tilstand til ACTIVE
                    Game updatedGame = new Game();
                    updatedGame.setUid(game.getUid());
                    updatedGame.setState(GameState.ACTIVE);

                    ResponseEntity<Game> response = client.put()
                            .uri("/game/" + game.getUid() + "/state")
                            .body(updatedGame)
                            .retrieve()
                            .toEntity(Game.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        System.out.println("Game started successfully");
                        // Genindlæs spil (valgfrit)
                        start(stage);
                    } else {
                        System.out.println("Failed to start game");
                    }
                } catch (Exception ex) {
                    System.out.println("Error starting game: " + ex.getMessage());
                }
            });

// Tilføj knappen til rækken
            HBox row = new HBox(10, gameInfo, joinButton, leaveButton, startButton);


            root.getChildren().add(row);
        }

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Game Sign-Up");
        stage.show();
    }
}
