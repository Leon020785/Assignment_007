package com.example.accessing_data_rest.repositories;

import java.util.List;

import com.example.accessing_data_rest.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "player", path = "player")
public interface PlayerRepository<Game> extends JpaRepository<Player, Long>, CrudRepository<Player,Long> {

    Player findByUid(long uid);

    String name(String name);

    long uid(long uid);

    List<Player> findByGame(Game game);

}
