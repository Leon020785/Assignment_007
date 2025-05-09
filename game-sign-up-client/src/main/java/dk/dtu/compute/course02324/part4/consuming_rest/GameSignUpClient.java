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
    private Label statusBarLabel;


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

        MenuItem startGameItem = new MenuItem("Start Game");
        startGameItem.setOnAction(e -> startGame());

        accountMenu.getItems().addAll(
                signUpItem,
                signInItem,
                createGameItem,
                joinGameItem,
                leaveGameItem,
                startGameItem // <-- Add the new menu item here
        );

        menuBar.getMenus().add(accountMenu);

        statusBarLabel = new Label("Welcome to RoboRally!");
        statusBarLabel.setPadding(new Insets(8));
        statusBarLabel.setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 12px; -fx-border-color: #d3d3d3; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        VBox vBox = new VBox(menuBar, statusBarLabel);
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
        styleButton(createButton);
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
        styleButton(cancelButton);
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
        styleButton(signInButton);
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
        styleButton(cancelButton);
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
                updateStatus("Signed in as: " + user.getName(), false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Sign in failed.");
                updateStatus("Sign in failed.", true);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Sign in failed: " + e.getMessage());
            updateStatus("Sign in failed: " + e.getMessage(), true);
        }
    }

    private void createGame() {
        try {
            Game game = new Game();
            game.setName("New Game");
            game.setMinPlayers(2);
            game.setMaxPlayers(6);
            game.setStarted(false);
            game.setFinished(false);

            Game createdGame = webClient.post()
                    .uri("/roborally/games/create")
                    .bodyValue(game)
                    .retrieve()
                    .bodyToMono(Game.class)
                    .block();

            if (createdGame != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game created with ID: " + createdGame.getUid() + " and Name: " + createdGame.getName());
                updateStatus("Game created: " + createdGame.getName(), false);

                // Auto-refresh game browser if open
                if (this.gameListViewRef != null) {
                    refreshGameList(this.gameListViewRef);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create game.");
                updateStatus("Failed to create game.", true);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create game: " + e.getMessage());
            updateStatus("Failed to create game: " + e.getMessage(), true);
        }
    }

    // ===================== GAME BROWSER IMPROVEMENTS =====================
    // Store the gameListView for refreshing after join/leave
    private ListView<String> gameListViewRef = null;

    private void openGameBrowser() {
        Stage gameBrowserStage = new Stage();
        gameBrowserStage.setTitle("Available Games");

        ListView<String> gameListView = new ListView<>();
        this.gameListViewRef = gameListView; // save reference for later refresh
        refreshGameList(gameListView);

        Button refreshButton = new Button("Refresh");
        styleButton(refreshButton);
        refreshButton.setTooltip(new Tooltip("Refresh the list of available games."));
        refreshButton.setOnAction(e -> refreshGameList(gameListView));

        VBox vbox = new VBox(10, gameListView, refreshButton);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 450, 400);
        gameBrowserStage.setScene(scene);
        gameBrowserStage.show();
    }

    private void refreshGameList(ListView<String> gameListView) {
        gameListView.getItems().clear();
        try {
            java.util.List<Game> games = webClient.get()
                    .uri("/roborally/games/opengames")
                    .retrieve()
                    .bodyToFlux(Game.class)
                    .collectList()
                    .block();

            if (games != null && !games.isEmpty()) {
                for (Game game : games) {
                    String state = game.getState().toUpperCase();
                    // Only show games that are in SIGNUP or READY state
                    if ("SIGNUP".equals(state) || "READY".equals(state)) {
                        String gameInfo = String.format("🕹️  ID: %-5d | Name: %-20s | Players: %2d/%-2d | State: %-10s",
                                game.getUid(), game.getName(), game.getPlayers().size(), game.getMaxPlayers(), state);
                        gameListView.getItems().add(gameInfo);
                    }
                }

                // Show a message if no open games were found
                if (gameListView.getItems().isEmpty()) {
                    gameListView.getItems().add("No open games available.");
                }
            } else {
                gameListView.getItems().add("No open games available.");
            }
        } catch (Exception e) {
            gameListView.getItems().add("Failed to load games: " + e.getMessage());
        }

        gameListView.setOnMouseClicked(event -> {
            String selectedItem = gameListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.startsWith("No open games")) {
                String[] parts = selectedItem.split("\\|");
                String gameIdPart = parts[0].trim();
                long gameId = Long.parseLong(gameIdPart.replace("🕹️", "").replace("ID:", "").trim());
                joinGameById(gameId);
            }
        });
    }

    private void joinGameById(long gameId) {
        if (signedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must be signed in to join a game.");
            updateStatus("Join game failed: not signed in.", true);
            return;
        }
        try {
            Player player = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/roborally/games/joingame")
                            .queryParam("gameid", gameId)
                            .build())
                    .bodyValue(signedInUser)
                    .retrieve()
                    .bodyToMono(Player.class)
                    .block();

            if (player != null) {
                // Fetch the full player object with game association
                joinedPlayer = webClient.get()
                        .uri("/roborally/players/" + player.getUid())
                        .retrieve()
                        .bodyToMono(Player.class)
                        .block();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Joined game as: " + joinedPlayer.getName());
                updateStatus("Joined game as: " + joinedPlayer.getName(), false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to join the game.");
                updateStatus("Failed to join the game.", true);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
            updateStatus("An unexpected error occurred.", true);
        }
        // Auto-refresh game browser if open
        if (this.gameListViewRef != null) {
            refreshGameList(this.gameListViewRef);
        }
    }

    private void joinGame() {
        // Open the improved game browser instead of text input dialog
        openGameBrowser();
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
            updateStatus("Leave game failed: not in a game.", true);
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
                    updateStatus("Left the game.", false);
                    joinedPlayer = null; // rydder spilleren
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to leave game: " + e.getMessage());
                    updateStatus("Failed to leave game: " + e.getMessage(), true);
                }
                // Auto-refresh game browser if open
                if (this.gameListViewRef != null) {
                    refreshGameList(this.gameListViewRef);
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

    private void updateStatus(String message, boolean isError) {
        statusBarLabel.setText("📢 " + message);
        if (isError) {
            statusBarLabel.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-size: 12px; -fx-border-color: #f5c6cb; -fx-border-radius: 3px; -fx-background-radius: 3px;");
        } else {
            statusBarLabel.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-size: 12px; -fx-border-color: #c3e6cb; -fx-border-radius: 3px; -fx-background-radius: 3px;");
        }
        statusBarLabel.setPadding(new Insets(8));
    }

    // Consistent button styling for all buttons
    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #007bff; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #0056b3; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #007bff; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: #003f7f; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        button.setOnMouseReleased(e -> button.setStyle("-fx-background-color: #0056b3; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
    }
    private void startGame() {
        if (joinedPlayer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must join a game before you can start it.");
            updateStatus("Start game failed: not joined a game.", true);
            return;
        }

        try {
            Game game = joinedPlayer.getGame();
            if (game == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No game associated with the current player.");
                updateStatus("Start game failed: no game associated.", true);
                return;
            }

            Game startedGame = webClient.post()
                    .uri("/roborally/games/startgame")
                    .bodyValue(game)
                    .retrieve()
                    .bodyToMono(Game.class)
                    .block();

            if (startedGame != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game started successfully: " + startedGame.getName());
                updateStatus("Game started: " + startedGame.getName(), false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to start the game.");
                updateStatus("Failed to start the game.", true);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to start the game: " + e.getMessage());
            updateStatus("Failed to start the game: " + e.getMessage(), true);
        }
    }
}