package ch.uzh.ifi.hase.soprafs22.repository;
import ch.uzh.ifi.hase.soprafs22.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("cardRepository") //repo inherits from JPA interface to where our User with its properties is stored
public interface CardRepository extends JpaRepository<Card,Long> {
    Card findByCardId(long cardId);
    List <Card> findByGameEditionAndIsWhite(String gameEdition,boolean isWhite);
    List <Card> findByDeckIdAndIsWhiteAndIsPlayed(long deckId,boolean isWhite,boolean isPlayed);

    List<Card> findByDeckId(long deckId);

}
