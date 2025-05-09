package com.example.accessing_data_rest.repositories;

import java.util.List;

import com.example.accessing_data_rest.model.Game;
import com.example.accessing_data_rest.model.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;




@RepositoryRestResource(collectionResourceRel = "game", path = "game")
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByName(@Param("name") String name);

    List<Game> findByStateIs(@Param("state") GameState state);

    Game findPlayerByUid(long uid);


    //long uid(long uid);

    long countByStateAndUid(GameState gameState, long gameId);



}
