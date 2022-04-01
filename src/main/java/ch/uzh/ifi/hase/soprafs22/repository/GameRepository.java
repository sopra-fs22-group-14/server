package ch.uzh.ifi.hase.soprafs22.repository;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRepository") //repo inherits from JPA interface to where our User with its properties is stored
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByGameId(long gameId);
    Game findByGameName(String gameName);

}
