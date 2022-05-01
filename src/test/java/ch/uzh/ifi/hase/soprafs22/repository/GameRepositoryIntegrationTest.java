package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void findGame_byID_success(){
        Game game=new Game();
        List<Long>playerIds=new ArrayList<>();
        List<Long>gameRoundIds=new ArrayList<>();

        playerIds.add(1L);
        playerIds.add(2L);
        playerIds.add(3L);
        playerIds.add(4L);

        gameRoundIds.add(1L);


        game.setCurrentGameRoundId(1L);
        game.setCurrentGameRoundIndex(0);
        game.setGameName("abc");
        game.setCardCzarMode(true);
        game.setDeckID(1L);
        game.setGameEdition("family");
        game.setNumOfRounds(8);
        game.setRoundIds(gameRoundIds);
        game.setPlayerIds(playerIds);
        game.setNumOfPlayersJoined(4);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found=gameRepository.findByGameId(game.getGameId());

        // then
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getCurrentGameRoundId(), game.getCurrentGameRoundId());
        assertEquals(found.getGameName(), game.getGameName());
        assertEquals(found.getCurrentGameRoundIndex(), game.getCurrentGameRoundIndex());
        assertEquals(found.isCardCzarMode(), game.isCardCzarMode());
        assertEquals(found.getDeckID(), game.getDeckID());
        assertEquals(found.getGameEdition(), game.getGameEdition());
        assertEquals(found.getNumOfRounds(), game.getNumOfRounds());

        assertEquals(found.getRoundIds(), game.getRoundIds());
        assertEquals(found.getPlayerIds(), game.getPlayerIds());
        assertEquals(found.getNumOfPlayersJoined(), game.getNumOfPlayersJoined());
    }


    @Test
    void findGame_byGameName_success(){
        Game game=new Game();
        List<Long>playerIds=new ArrayList<>();
        List<Long>gameRoundIds=new ArrayList<>();

        playerIds.add(1L);
        playerIds.add(2L);
        playerIds.add(3L);
        playerIds.add(4L);

        gameRoundIds.add(1L);


        game.setCurrentGameRoundId(1L);
        game.setCurrentGameRoundIndex(0);
        game.setGameName("abc");
        game.setCardCzarMode(true);
        game.setDeckID(1L);
        game.setGameEdition("family");
        game.setNumOfRounds(8);
        game.setRoundIds(gameRoundIds);
        game.setPlayerIds(playerIds);
        game.setNumOfPlayersJoined(4);

        entityManager.persist(game);
        entityManager.flush();

        // when
        Game found=gameRepository.findByGameName(game.getGameName());

        // then
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getCurrentGameRoundId(), game.getCurrentGameRoundId());
        assertEquals(found.getGameName(), game.getGameName());
        assertEquals(found.getCurrentGameRoundIndex(), game.getCurrentGameRoundIndex());
        assertEquals(found.isCardCzarMode(), game.isCardCzarMode());
        assertEquals(found.getDeckID(), game.getDeckID());
        assertEquals(found.getGameEdition(), game.getGameEdition());
        assertEquals(found.getNumOfRounds(), game.getNumOfRounds());

        assertEquals(found.getRoundIds(), game.getRoundIds());
        assertEquals(found.getPlayerIds(), game.getPlayerIds());
        assertEquals(found.getNumOfPlayersJoined(), game.getNumOfPlayersJoined());


    }




}
