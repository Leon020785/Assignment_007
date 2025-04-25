package dk.dtu.compute.course02324.part4.consuming_rest;

import org.springframework.web.client.RestClient;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Player;
import dk.dtu.compute.course02324.part4.consuming_rest.model.Game;
import dk.dtu.compute.course02324.part4.consuming_rest.model.User;

public class OnlineController {

    private final RestClient restClient;

    public OnlineController() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:8080/")
                .build();
    }
    // OnlineController er en klasse i client, der håndterer al
    // kommunikationen med backend via REST

    public void joinGame(User user, Game game, String playerName) {
        // opretter en ny spiller navn,user og game
        Player player = new Player();
        player.setName(playerName);
        player.setUser(user);
        player.setGame(game);

        try {
            // sender spiller object til backend Post / spiller
            Player created = restClient.post()
                    .uri("/player")
                    .body(player)
                    .retrieve()
                    .body(Player.class);

            System.out.println("Player created: " + created);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to join game.");
        }
    }

     // This is used when a user wants to leave a game.
    public void leaveGame(Player player) {
        try {
            restClient.delete()
                    .uri("/player/" + player.getUid())
                    .retrieve();
            System.out.println("Player removed: " + player);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to leave game.");
        }
    }

}

