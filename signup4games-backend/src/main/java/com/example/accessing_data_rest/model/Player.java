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

    // FIXME the ID of this could actually be the two foreign keys game_id and
    //       user_id, but this is a bit tricky to start with. So this will
    //       Not be done in the context of course 02324!
    @Id
    @Column(name = "player_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long uid;

    private String name;

    /**
     * Reference to the game this player is participating in.
     */
    @ManyToOne
    @JoinColumn(name = "game_id") // Optional: specific column name
    private Game game;

    /**
     * Reference to the user who owns this player instance.
     */
    @ManyToOne
    @JoinColumn(name = "user_id") // Must match User's @OneToMany
    private User user;

    // --- Getters and Setters ---

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid; // Fixed: previously used wrong variable name
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
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
