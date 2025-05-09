package dk.dtu.compute.course02324.part4.consuming_rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private Long uid;
    private String name;
    private Long gameId;  // <-- Vi gemmer kun game ID
    private Long userId;  // <-- Vi gemmer kun user ID

    // Getters og setters

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    // Proper getGame method to retrieve the actual Game object from the backend
    public Game getGame() {
        if (this.gameId == null) {
            return null;
        }
        try {
            org.springframework.web.reactive.function.client.WebClient webClient =
                    org.springframework.web.reactive.function.client.WebClient.create("http://localhost:8080");
            return webClient.get()
                    .uri("/roborally/games/" + this.gameId)
                    .retrieve()
                    .bodyToMono(Game.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", gameId=" + gameId +
                ", userId=" + userId +
                '}';
    }
}
