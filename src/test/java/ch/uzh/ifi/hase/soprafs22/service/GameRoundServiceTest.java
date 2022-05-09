package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameRoundServiceTest {
    @Mock
    GameRepository gameRepository;
    @Mock
    GameRoundRepository gameRoundRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    CardRepository cardRepository;
    @Mock
    DeckRepository deckRepository;
    @InjectMocks
    private GameRoundService gameRoundService;

    @Mock
    PlayerRepository playerRepository;
    @InjectMocks
    private User testUser;
    @InjectMocks
    private User testUser2;
    @InjectMocks
    private User testUser3;
    @InjectMocks
    private User testUser4;
    @InjectMocks
    private Game testGame;
    @InjectMocks
    private Player testPlayer;
    @InjectMocks
    private Player testPlayer2;
    @InjectMocks
    private Player testPlayer3;
    @InjectMocks
    private Player testPlayer4;

    @InjectMocks
    private Card testCard;
    @InjectMocks
    private Card testCard2;
    @InjectMocks
    private Card testCard3;

    @InjectMocks
    private Deck testDeck;
    @InjectMocks
    private GameRound testRound;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("testToken");

        testUser2=new User();
        testUser2.setUserId(2L);
        testUser2.setPassword("testPassword");
        testUser2.setUsername("testUsername2");
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setToken("testToken2");

        testUser3=new User();
        testUser3.setUserId(6L);
        testUser3.setPassword("testPassword");
        testUser3.setUsername("testUsername3");
        testUser3.setStatus(UserStatus.OFFLINE);
        testUser3.setToken("testToken3");

        testUser4=new User();
        testUser3.setUserId(9L);
        testUser3.setPassword("testPassword");
        testUser3.setUsername("testUsername4");
        testUser3.setStatus(UserStatus.OFFLINE);
        testUser3.setToken("testToken4");


        testPlayer=new Player();
        testPlayer.setPlayerId(1L);
        testPlayer.setPlayerName("testUsername");
        testPlayer.setPlaying(true);
        testPlayer.setCardCzar(true);

        testPlayer2=new Player();
        testPlayer2.setPlayerId(2L);
        testPlayer2.setPlayerName("testUsername2");
        testPlayer2.setPlaying(true);
        testPlayer2.setCardCzar(false);
        testPlayer2.setNumberOfPicked(0);

        testPlayer3=new Player();
        testPlayer3.setPlayerId(6L);
        testPlayer3.setPlayerName("testUsername3");
        testPlayer3.setPlaying(true);
        testPlayer3.setCardCzar(false);

        testPlayer4=new Player();
        testPlayer4.setPlayerId(9L);
        testPlayer4.setPlayerName("testUsername4");
        testPlayer4.setPlaying(true);
        testPlayer4.setCardCzar(false);




        testGame=new Game();
        testGame.setGameId(3L);
        testGame.setGameName("testGame");
        testGame.setNumOfPlayersJoined(1);
        testGame.setNumOfRounds(8);
        testGame.setCardCzarMode(true);
        testGame.setCurrentGameRoundIndex(0);
        testGame.setGameEdition("family");
        testGame.setDeckID(4L);
        testGame.setCurrentGameRoundId(8L);
        List<Long> testGamePlayerIds=new ArrayList<>();
        testGamePlayerIds.add(1L);
        testGame.setPlayerIds(testGamePlayerIds);

        testCard=new Card();
        testCard.setCardId(4L);
        testCard.setCardText("testCard");
        testCard.setGameEdition("family");
        testCard.setDeckId(5L);
        testCard.setWhite(true);
        testCard.setPlayed(false);

        testCard2=new Card();
        testCard2.setCardId(7L);
        testCard2.setCardText("testBlackCard");
        testCard2.setGameEdition("family");
        testCard2.setDeckId(5L);
        testCard2.setWhite(false);
        testCard2.setPlayed(false);

        testCard3=new Card();
        testCard3.setCardId(10L);
        testCard3.setCardText("testCard2");
        testCard3.setGameEdition("family");
        testCard3.setDeckId(5L);
        testCard3.setWhite(true);
        testCard3.setPlayed(false);

        List<Card>testCards=new ArrayList<>();
        testCards.add(testCard);

        testDeck=new Deck();
        testDeck.setDeckId(5L);
        testDeck.setDeckName("family");
        testDeck.setCards(testCards);
        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        testRound=new GameRound();
        testRound.setRoundId(8L);
        testRound.setCardCzarId(1L);
        testRound.setBlackCard(testCard2);
        testRound.setCorrespondingGameId(3L);
        testRound.setFinal(false);

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser2);
        //Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
        //Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer2);


    }

    @Test
    public void startNewRound_cardCzar_success(){
        List<Card>testCards=new ArrayList<>();
        List<Card>blackCards=new ArrayList<>();
        testCards.add(testCard);
        testCards.add(testCard2);
        blackCards.add(testCard2);
        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(testGame.getDeckID(),true,false)).thenReturn(testCards);
        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testCard);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(deckRepository.findByDeckId(testGame.getDeckID())).thenReturn(testDeck);
        Mockito.when(cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(testGame.getDeckID(),false,false)).thenReturn(blackCards);
        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testCard2);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
         Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        GameRound startedRound=gameRoundService.startNewRound(testGame);
        assertEquals(testRound.getRoundId(), startedRound.getRoundId());




    }
    @Test
    public void createNewRound_success(){
        List<Card>blackCards=new ArrayList<>();
        blackCards.add(testCard2);
        Mockito.when(deckRepository.findByDeckId(testGame.getDeckID())).thenReturn(testDeck);
        Mockito.when(cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(testGame.getDeckID(),false,false)).thenReturn(blackCards);
        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testCard2);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        Mockito.when(gameRepository.saveAndFlush(Mockito.any())).thenReturn(testGame);
        GameRound createdGameRound=gameRoundService.createNewRound(testGame);
        assertEquals(testRound.getRoundId(), createdGameRound.getRoundId());

    }
    @Test
    public void createNewRound_noMoreGameRounds_throwsException(){
        List<Long>testRoundIds=new ArrayList<>();
        testRoundIds.add(11L);
        testRoundIds.add(12L);
        testRoundIds.add(13L);
        testRoundIds.add(14L);
        testRoundIds.add(15L);
        testRoundIds.add(16L);
        testRoundIds.add(17L);
        testRoundIds.add(18L);
        testGame.setRoundIds(testRoundIds);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameRoundService.createNewRound(testGame));
        String exceptionMessage = "Game has finished! No more gameRounds.";
        assertEquals(exceptionMessage,exception.getReason());


    }
    @Test
    public void chooseRoundWinner_success(){
        Map<Long,Long>testCardAndPlayerIds=new HashMap<>();
        testCardAndPlayerIds.put(4L,2L);
        testRound.setCardAndPlayerIds(testCardAndPlayerIds);
        Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
        Mockito.when(playerRepository.findByPlayerId(testUser.getUserId())).thenReturn(testPlayer);
        Mockito.when(gameRoundRepository.findByRoundId(testRound.getRoundId())).thenReturn(testRound);
        Mockito.when(playerRepository.findByPlayerId(2L)).thenReturn(testPlayer2);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer2);
        Mockito.when(gameRoundRepository.saveAndFlush(Mockito.any())).thenReturn(testRound);
        Mockito.when(gameRepository.findByGameId(testRound.getCorrespondingGameId())).thenReturn(testGame);
        List<Card>testCards=new ArrayList<>();
        List<Card>blackCards=new ArrayList<>();
        testCards.add(testCard);
        testCards.add(testCard2);
        blackCards.add(testCard2);
        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(testGame.getDeckID(),true,false)).thenReturn(testCards);
        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testCard);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(deckRepository.findByDeckId(testGame.getDeckID())).thenReturn(testDeck);
        Mockito.when(cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(testGame.getDeckID(),false,false)).thenReturn(blackCards);
        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testCard2);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        String foundName=gameRoundService.chooseRoundWinner(testRound.getRoundId(),"testToken",4L);
        assertEquals(foundName,testPlayer2.getPlayerName());



    }
    @Test
    public void chooseRoundWinner_notCardCzar_throwsException(){
        Mockito.when(userRepository.findByToken("testToken2")).thenReturn(testUser2);
        Mockito.when(playerRepository.findByPlayerId(testUser2.getUserId())).thenReturn(testPlayer2);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameRoundService.chooseRoundWinner(testRound.getRoundId(),"testToken2",4L));
        String exceptionMessage = "player is not card Czar";
        assertEquals(exceptionMessage,exception.getReason());
    }
     @Test
    public void updateLatestRoundWinner_success(){

        Mockito.when(gameRoundRepository.findByRoundId(testRound.getRoundId())).thenReturn(testRound);
        Mockito.when(gameRepository.findByGameId(testRound.getCorrespondingGameId())).thenReturn(testGame);
        Mockito.when(cardRepository.findByCardId(testCard.getCardId())).thenReturn(testCard);
        gameRoundService.updateLatestRoundWinner("testWinner",testRound.getRoundId(),testCard.getCardId());

    }
    @Test
    public void pickCard_success(){
        Map<Long,Long>testCardAndPlayerIds=new HashMap<>();
        testCardAndPlayerIds.put(4L,2L);
        testRound.setCardAndPlayerIds(testCardAndPlayerIds);
        testRound.setNumberOfPicked(3);
        Mockito.when(gameRoundRepository.findByRoundId(testRound.getRoundId())).thenReturn(testRound);
        Mockito.when(playerRepository.findByPlayerId(2L)).thenReturn(testPlayer2);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer2);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        Mockito.when(gameRepository.findByGameId(testRound.getCorrespondingGameId())).thenReturn(testGame);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        List<Card>testCards=new ArrayList<>();
        List<Card>blackCards=new ArrayList<>();
        testCards.add(testCard);
        testCards.add(testCard2);
        blackCards.add(testCard2);
        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(testGame.getDeckID(),true,false)).thenReturn(testCards);
        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testCard);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(deckRepository.findByDeckId(testGame.getDeckID())).thenReturn(testDeck);
        Mockito.when(cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(testGame.getDeckID(),false,false)).thenReturn(blackCards);
        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testCard2);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(playerRepository.findByPlayerId(testPlayer.getPlayerId())).thenReturn(testPlayer);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        gameRoundService.pickCard(testRound.getRoundId(),"testToken3",4L);

    }
    @Test
    public void playCard_success(){
        List<Card> testCards=new ArrayList<>();
        testCards.add(testCard2);
        testPlayer2.setCardsOnHands(testCards);
        Mockito.when(userRepository.findByToken("testToken2")).thenReturn(testUser2);
        Mockito.when(gameRoundRepository.findByRoundId(testRound.getRoundId())).thenReturn(testRound);
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(playerRepository.findByPlayerId(testUser2.getUserId())).thenReturn(testPlayer2);
        Mockito.when(cardRepository.findByCardId(testCard2.getCardId())).thenReturn(testCard2);
        Mockito.when(gameRoundRepository.save(Mockito.any())).thenReturn(testRound);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer2);
        GameRound playedRound=gameRoundService.playCard(testRound.getRoundId(),"testToken2",testCard2.getCardId(),testGame.getGameId());
        assertEquals(playedRound.getRoundId(),testRound.getRoundId());

    }
    @Test
    public void playCard_cardCzarNotPlay_throwsException(){
        Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
        Mockito.when(gameRoundRepository.findByRoundId(testRound.getRoundId())).thenReturn(testRound);
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(playerRepository.findByPlayerId(testUser.getUserId())).thenReturn(testPlayer);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameRoundService.playCard(testRound.getRoundId(),"testToken",7L,testGame.getGameId()));
        String exceptionMessage = "card czar can not play a card!";
        assertEquals(exceptionMessage,exception.getReason());

    }
    @Test
    public void playCard_alreadyPlayed_throwsException(){
        Map<Long,Long>testCardAndPlayerIds=new HashMap<>();
        testCardAndPlayerIds.put(4L,2L);
        testRound.setCardAndPlayerIds(testCardAndPlayerIds);
        Mockito.when(userRepository.findByToken("testToken2")).thenReturn(testUser2);
        Mockito.when(gameRoundRepository.findByRoundId(testRound.getRoundId())).thenReturn(testRound);
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(playerRepository.findByPlayerId(testUser2.getUserId())).thenReturn(testPlayer2);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameRoundService.playCard(testRound.getRoundId(),"testToken2",4L,testGame.getGameId()));
        String exceptionMessage = "player already played a card!";
        assertEquals(exceptionMessage,exception.getReason());
    }




}
