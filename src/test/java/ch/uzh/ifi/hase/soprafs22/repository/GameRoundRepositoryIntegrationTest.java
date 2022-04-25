package ch.uzh.ifi.hase.soprafs22.repository;
import ch.uzh.ifi.hase.soprafs22.entity.Card;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.GameRound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)


public class GameRoundRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRoundRepository gameRoundRepository;

    @Test
    void findGameRound_byID_success(){
        GameRound gameRound=new GameRound();


        gameRound.setCardCzarId(3L);

        Card testCard=new Card();
        testCard.setPlayed(true);
        testCard.setDeckId(6L);
        testCard.setWhite(false);
        testCard.setGameEdition("family");
        testCard.setCardText("blacktest");

        Card testCard2=new Card();
        testCard2.setPlayed(true);
        testCard2.setDeckId(6L);
        testCard2.setWhite(true);
        testCard2.setGameEdition("family");
        testCard2.setCardText("whitetest");

        Card testCard3=new Card();
        testCard3.setPlayed(true);
        testCard3.setDeckId(6L);
        testCard3.setWhite(true);
        testCard3.setGameEdition("family");
        testCard3.setCardText("whitetest");

        Card testCard4=new Card();
        testCard4.setPlayed(true);
        testCard4.setDeckId(6L);
        testCard4.setWhite(true);
        testCard4.setGameEdition("family");
        testCard4.setCardText("whitetest");


        entityManager.persist(testCard);
        entityManager.persist(testCard2);
        entityManager.persist(testCard3);
        entityManager.persist(testCard4);
        entityManager.flush();
        gameRound.setBlackCard(testCard);

        List<Card> testPlayedCards=new ArrayList<>();
        testPlayedCards.add(testCard2);
        testPlayedCards.add(testCard3);
        testPlayedCards.add(testCard4);
        gameRound.setPlayedCards(testPlayedCards);

        entityManager.persist(gameRound);
        entityManager.flush();

        // when
        GameRound found=gameRoundRepository.findByRoundId(gameRound.getRoundId());

        assertEquals(found.getRoundId(), gameRound.getRoundId());
        assertEquals(found.getBlackCard(), gameRound.getBlackCard());
        assertEquals(found.getCardCzarId(), gameRound.getCardCzarId());
        assertEquals(found.getPlayedCards(), gameRound.getPlayedCards());



    }




}
