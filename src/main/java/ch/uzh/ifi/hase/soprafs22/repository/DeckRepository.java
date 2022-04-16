package ch.uzh.ifi.hase.soprafs22.repository;
import ch.uzh.ifi.hase.soprafs22.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("deckRepository") //repo inherits from JPA interface to where our User with its properties is stored
public interface DeckRepository extends JpaRepository<Deck,Long> {
    Deck findByDeckId(long deckId);


}
