package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.wrappers.HALWrapperGames;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import javafx.application.Application;

import java.util.List;


public class GameSignUpClient extends Application {

    // see https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-resttemplate

    public static void main(String[] args) {


        /* Before you start this make sure you have created a player (with uid=1) and
         * a game (with uid=1) in the database; you can do that via the command
         * line tool curl, the tool Postman or  the HAL explorer (which, after
         * your have started your backend is available at http://localhost:8080/api).
         *
         * You can create a player by posting
         *
         *   {
         *     "maxPlayers": 2,
         *     "minPlayers": 6,
         *     "name": "First Game"
         *    }
         *
         * to http://localhost:8080/game
         *
         * and by posting
         *
         *   {
         *     "name": "Player 1"
         *   }
         *
         * to ttp://localhost:8080/game
         *
         */


        launch(args);

//        RestClient customClient = RestClient.builder().
//                // requestFactory(new HttpComponentsClientHttpRequestFactory()).
//                baseUrl("http://localhost:8080").
//                build();
//
//        // String result = customClient.get().uri("/game").retrieve().body(String.class);
//        String result = customClient.get().uri("/").retrieve().body(String.class);
//
//        System.out.println(result);
//
//        System.out.println("---------------------------------------------------------");
//
//        result = customClient.get().uri("/game").retrieve().body(String.class);
//
//        System.out.println(result);
//
//        System.out.println("---------------------------------------------------------");
//
//
//        Game game1 = customClient.get().uri("/game/1").retrieve().body(Game.class);
//
//        System.out.println("Game with uid 1 is: " + game1);
//
//        System.out.println("---------------------------------------------------------");
//
//        List<Game> games = customClient.get().uri("/game").retrieve().body(HALWrapperGames.class).getGames();
//
//        for (Game game: games) {
//            System.out.println(game);
//        }
//
//        System.out.println("---------------------------------------------------------");
//
//        Player player1 = customClient.get().uri("/player/1").retrieve().body(Player.class);
//
//        System.out.println("Player with uid 1 is: " + player1);
//
//
//        System.out.println("---------------------------------------------------------");
//
//        // the following put request will connect game1 with player1:
//
//        String body = "http://localhost:8080/game/1";
//
//        ResponseEntity<Player> playerResponseEntity = customClient.put().uri("/player/1/game").
//                header("Content-Type", "text/uri-list").
//                body(body).retrieve().toEntity(Player.class);
//        System.out.println("player: " + playerResponseEntity.toString());
//
//
//        System.out.println("---------------------------------------------------------");
//
//        game1 = customClient.get().uri("/player/1/game").retrieve().body(Game.class);
//
//        System.out.println("Game attached to Player with uid 1 is: " + game1);




        // TODO try to read out the available games from the backend, show them on a
        //      simple graphical GUI and sign up for a game using some of the operations
        //      at the top.
        //      For the GUI to work in JavaFX, you need to add some maven dependencies
        //      (see pom file for Assignment 3).

    }

    @Override
    public void start(Stage stage) throws Exception {


        VBox root = new VBox();
        Label title = new Label("Game Sign Up Client");
        root.getChildren().add(title);
        root.setSpacing(10);

        RestClient client = RestClient.builder().baseUrl("http://localhost:8080").build();
        List<Game> games = client.get().uri("/game").retrieve().body(HALWrapperGames.class).getGames();

        for (Game game : games) {
            TextField field = new TextField("Game: " + game.getName() + ", min: " + game.getMinPlayers() + ", max: " + game.getMaxPlayers());
            field.setEditable(false);
            root.getChildren().add(field);
        }

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Game Sign-Up");
        stage.setScene(scene);
        stage.show();

    }
}
