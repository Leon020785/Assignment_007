package com.example.accessing_data_rest.repositories;

import com.example.accessing_data_rest.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "player", path = "player")
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Find a player by their UID
    Player findByUid(@Param("uid") long uid);

    // Find players by their name
    List<Player> findByName(@Param("name") String name);

    // Find all players associated with a specific game
    List<Player> findByGameUid(@Param("gameUid") long gameUid);
}