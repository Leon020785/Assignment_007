package dk.dtu.compute.course02324.part4.consuming_rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)  // Meget vigtigt!
public class User {

    private Long uid;    // Sørg for "Long" med stort L
    private String name;
    private List<Player> players;

    // Getters and Setters
    public Long getUid() {   // Bare returner Long direkte
        return uid;
    }

    public void setUid(Long uid) {  // Modtag Long
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

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", players=" + (players != null ? players.size() : 0) +
                '}';
    }
}
