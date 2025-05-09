package com.example.accessing_data_rest.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Player entity represents a user in a specific game.
 * Connected to both a Game and a User entity.
 */
@Entity
@JsonIdentityInfo(
        scope = Player.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "uid"
)
public class Player {

    @Id
    @Column(name = "player_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long uid;

    private String name;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- Getters and Setters ---

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        this.user = user;
    }

    @Override
    public String toString() {
        return "Player{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", game=" + (game != null ? game.getUid() : "null") +
                ", user=" + (user != null ? user.getUid() : "null") +
                '}';
    }
}
