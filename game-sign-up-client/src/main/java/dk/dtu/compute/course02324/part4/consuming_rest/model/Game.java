package dk.dtu.compute.course02324.part4.consuming_rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private long uid;

    private String name;

    private int minPlayers;

    private int maxPlayers;

    // TODO There could be more attributes here, kie
    //      in which state is the sign up for the game, did
    //      the game started or finish (after the game started
    //      you might not want new players coming in etc.)
    //      See analogous classes in backend.

    private boolean started;
    private boolean finished;

    private List<Player> players;

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

    public List<Player> getPlayers() {
        return players;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }


    @Override
    public String toString() {
        return "Game{" + "uid=" + uid + ", name='" + name + '\'' + ", minPlayers=" + minPlayers + ", maxPlayers=" + maxPlayers +
                // ", players=" + players +
                ", started=" + started + ", finished=" + finished + '}';
    }
}
