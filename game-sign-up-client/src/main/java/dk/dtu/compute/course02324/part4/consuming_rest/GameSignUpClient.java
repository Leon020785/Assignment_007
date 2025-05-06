package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.PlayerRequest;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.web.reactive.function.client.WebClient;

public class GameSignUpClient extends Application {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    private User signedInUser;
    private Player joinedPlayer; // <-- nyt felt til at holde den aktive spiller
    ;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game SetUp");

        MenuBar menuBar = new MenuBar();
        Menu accountMenu = new Menu("Account");

        MenuItem signUpItem = new MenuItem("Sign Up");
        signUpItem.setOnAction(e -> openSignUpWindow());

        MenuItem signInItem = new MenuItem("Sign In");
        signInItem.setOnAction(e -> openSignInWindow());

        MenuItem leaveGameItem = new MenuItem("Leave Game"); // <-- tilføjet Leave Game
        leaveGameItem.setOnAction(e -> leaveGame());         // <-- tilføjet handler


        MenuItem createGameItem = new MenuItem("Create Game");
        createGameItem.setOnAction(e -> createGame());

        MenuItem joinGameItem = new MenuItem("Join Game");
        joinGameItem.setOnAction(e -> joinGame());

        accountMenu.getItems().addAll(
                signUpItem,
                signInItem,
                createGameItem,
                joinGameItem,
                leaveGameItem
        );

        menuBar.getMenus().add(accountMenu);

        VBox vBox = new VBox(menuBar);
        Scene scene = new Scene(vBox, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openSignUpWindow() {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                signUp(name);
                signUpStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Name cannot be empty.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> signUpStage.close());

        HBox buttonBox = new HBox(10, createButton, cancelButton);

        vbox.getChildren().addAll(new Label("Sign Up"), nameField, buttonBox);

        Scene scene = new Scene(vbox);
        signUpStage.setScene(scene);
        signUpStage.show();
    }

    private void openSignInWindow() {
        Stage signInStage = new Stage();
        signInStage.setTitle("Sign In");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField idField = new TextField();
        idField.setPromptText("Enter your User ID");

        Button signInButton = new Button("Sign In");
        signInButton.setOnAction(e -> {
            try {
                long userId = Long.parseLong(idField.getText());
                signIn(userId);
                signInStage.close();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid User ID.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> signInStage.close());

        HBox buttonBox = new HBox(10, signInButton, cancelButton);

        vbox.getChildren().addAll(new Label("Sign In"), idField, buttonBox);

        Scene scene = new Scene(vbox);
        signInStage.setScene(scene);
        signInStage.show();
    }



    private void signIn(long userId) {
        try {
            User user = webClient.get()
                    .uri("/user/{id}", userId)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();

            if (user != null) {
                signedInUser = user;
                showAlert(Alert.AlertType.INFORMATION, "Success", "Signed in as: " + user.getName());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Sign in failed.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Sign in failed: " + e.getMessage());
        }
    }

    private void createGame() {
        try {
            Game createdGame = webClient.post()
                    .uri("/roborally/games/create")
                    .retrieve()
                    .bodyToMono(Game.class)
                    .block();

            if (createdGame != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game created with ID: " + createdGame.getUid());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create game.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create game: " + e.getMessage());
        }
    }

    private void joinGame() {
        if (signedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must be signed in to join a game.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Join Game");
        dialog.setHeaderText("Join a Game");
        dialog.setContentText("Enter Game ID:");

        dialog.showAndWait().ifPresent(gameIdStr -> {
            try {
                long gameId = Long.parseLong(gameIdStr);

                joinedPlayer = webClient.post() // <-- ændret: gemmer spilleren i en feltvariabel
                        .uri(uriBuilder -> uriBuilder
                                .path("/roborally/games/joingame")
                                .queryParam("gameid", gameId)
                                .build())
                        .bodyValue(signedInUser)
                        .retrieve()
                        .bodyToMono(Player.class)
                        .block();

                if (joinedPlayer != null) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Joined game as: " + joinedPlayer.getName());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to join the game.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Game ID.");
            }
        });
    }
    private void signUp(String name) {
        try {
            User user = new User();
            user.setName(name);

            User createdUser = webClient.post()
                    .uri("/user")
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();

            if (createdUser != null) {
                signedInUser = createdUser;
                showAlert(Alert.AlertType.INFORMATION, "Success", "Successfully signed up as: " + createdUser.getName() + " User ID: " + createdUser.getUid());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Sign up failed.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Sign up failed: " + e.getMessage());
        }
    }

    private void leaveGame() {
        if (joinedPlayer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "You are not currently in a game.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Leave Game");
        confirm.setHeaderText("Are you sure you want to leave the game?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    webClient.delete()
                            .uri("/roborally/games/player/" + joinedPlayer.getUid())
                            .retrieve()
                            .toBodilessEntity()
                            .block();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "You have left the game.");
                    joinedPlayer = null; // rydder spilleren
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to leave game: " + e.getMessage());
                }
            }
        });
    }





    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}