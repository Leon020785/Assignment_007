package com.example.accessing_data_rest.model;

/**
 * Data Transfer Object (DTO) for player creation requests.
 */
public class PlayerRequest {

    private String user;

    // --- Getters and Setters ---

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User identifier cannot be null or empty.");
        }
        this.user = user;
    }

    @Override
    public String toString() {
        return "PlayerRequest{" +
                "user='" + user + '\'' +
                '}';
    }
}
