package ch.uzh.ifi.hase.soprafs22.repository;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRoundRepository") //repo inherits from JPA interface to where our User with its properties is stored
public interface GameRoundRepository extends JpaRepository<GameRound, Long> {
    Game findByRoundId(long roundId);
}
