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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
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
    private GameService gameService;

    @Mock
    PlayerRepository playerRepository;
    @InjectMocks
    private User testUser;
    @InjectMocks
    private User testUser2;
    @InjectMocks
    private User testUser3;
    @InjectMocks
    private Game testGame;
    @InjectMocks
    private Player testPlayer;
    @InjectMocks
    private Player testPlayer2;
    @InjectMocks
    private Player testPlayer3;
    @InjectMocks
    private Card testCard;
    @InjectMocks
    private Card testCard2;
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

        testPlayer=new Player();
        testPlayer.setPlayerId(1L);
        testPlayer.setPlayerName("testUsername");
        testPlayer.setPlaying(true);
        testPlayer.setCardCzar(false);

        testPlayer2=new Player();
        testPlayer2.setPlayerId(2L);
        testPlayer2.setPlayerName("testUsername2");
        testPlayer2.setPlaying(true);
        testPlayer2.setCardCzar(false);

        testPlayer3=new Player();
        testPlayer3.setPlayerId(6L);
        testPlayer3.setPlayerName("testUsername3");
        testPlayer3.setPlaying(true);
        testPlayer3.setCardCzar(false);




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
        testCard2.setPlayed(true);

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

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser2);
        //Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
        //Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer2);


    }
    @Test
    public void createGame_validInputs_success(){
        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(cardRepository.save(Mockito.any())).thenReturn(testCard);
        Mockito.when(deckRepository.save(Mockito.any())).thenReturn(testDeck);
        Mockito.when(gameRepository.saveAndFlush(Mockito.any())).thenReturn(testGame);
        Game createdGame=gameService.createNewGame(testGame,"testToken");
        assertEquals(testGame.getGameId(), createdGame.getGameId());
        assertEquals(testGame.getGameEdition(), createdGame.getGameEdition());
        assertEquals(testGame.getNumOfPlayersJoined(), createdGame.getNumOfPlayersJoined());
        assertEquals(testGame.getPlayerIds(),createdGame.getPlayerIds());
        assertEquals(testGame.getDeckID(),createdGame.getDeckID());
        assertEquals(testGame.isCardCzarMode(),createdGame.isCardCzarMode());
        assertEquals(testGame.getCurrentGameRoundIndex(),createdGame.getCurrentGameRoundIndex());
        assertEquals(testGame.getNumOfRounds(),createdGame.getNumOfRounds());
        assertEquals(testGame.getGameName(),createdGame.getGameName());

    }
    @Test
    public void createGame_duplicateInputs_throwException(){
        String exceptionMessage = "GameName is already taken!";
        Mockito.when(gameRepository.findByGameName(testGame.getGameName())).thenReturn(testGame);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.createNewGame(testGame,"testToken"));
        assertEquals(exceptionMessage,exception.getReason());

    }
    @Test
    public void joinGame_validInputs_success(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(userRepository.findByToken(testUser2.getToken())).thenReturn(testUser2);
        Mockito.when(playerRepository.saveAndFlush(Mockito.any())).thenReturn(testPlayer2);
        Game joinedGame=gameService.joinGame(testGame.getGameId(),"testToken2");
    }
    @Test
    public void joinGame_notLoggedIn_throwsException(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(userRepository.findByToken(testUser3.getToken())).thenReturn(testUser3);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.joinGame(testGame.getGameId(),"testToken3"));
        String exceptionMessage = "User is not logged in, cannot join a game!";
        assertEquals(exceptionMessage,exception.getReason());
    }
    @Test
    public void joinGame_playerAlreadyJoined_throwsException(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.joinGame(testGame.getGameId(),"testToken"));
        String exceptionMessage = "The user is already in the game!";
        assertEquals(exceptionMessage,exception.getReason());

    }

    @Test
    public void getGame_validInput_success(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Game foundGame=gameService.getGame(testGame.getGameId());
        assertEquals(foundGame.getGameId(), testGame.getGameId());
        assertEquals(foundGame.getGameEdition(), testGame.getGameEdition());
        assertEquals(foundGame.getNumOfPlayersJoined(), testGame.getNumOfPlayersJoined());
        assertEquals(foundGame.getPlayerIds(),testGame.getPlayerIds());
        assertEquals(foundGame.getDeckID(),testGame.getDeckID());
        assertEquals(foundGame.isCardCzarMode(),testGame.isCardCzarMode());
        assertEquals(foundGame.getCurrentGameRoundIndex(),testGame.getCurrentGameRoundIndex());
        assertEquals(foundGame.getNumOfRounds(),testGame.getNumOfRounds());
        assertEquals(foundGame.getGameName(),testGame.getGameName());

    }
    @Test
    public void getGame_notFound_throwsException(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.getGame(testGame.getGameId()));
        String exceptionMessage = "Game with given Id doesn't exist!";
        assertEquals(exceptionMessage,exception.getReason());

    }
    @Test
    public void updatePlayerCount_validInput_success(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Game updatedGame=gameService.updatePlayerCount(testGame.getGameId(),"testToken");
    }
    @Test
    public void updatePlayerCount_notJoined_throwsException(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(userRepository.findByToken(testUser2.getToken())).thenReturn(testUser2);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.updatePlayerCount(testGame.getGameId(),"testToken2"));
        String exceptionMessage = "User is not in this game, join first!";
        assertEquals(exceptionMessage,exception.getReason());

    }
    @Test
    public void getPlayer_success(){
        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(playerRepository.findByPlayerId(testUser.getUserId())).thenReturn(testPlayer);
        Player foundPlayer=gameService.getPlayer(testUser.getToken());
        assertEquals(foundPlayer.getPlayerId(),testPlayer.getPlayerId());
        assertEquals(foundPlayer.getPlayerName(),testPlayer.getPlayerName());
        assertEquals(foundPlayer.getRoundsWon(),testPlayer.getRoundsWon());
        assertEquals(foundPlayer.getCardsOnHands(),testPlayer.getCardsOnHands());

    }

    @Test
    public void getGameRound_success(){
        Mockito.when(gameRepository.findByGameId(testGame.getGameId())).thenReturn(testGame);
        Mockito.when(gameRoundRepository.findByRoundId(testGame.getCurrentGameRoundId())).thenReturn(testRound);
        GameRound foundGameRound=gameService.getGameRound(testGame.getGameId());
        assertEquals(foundGameRound.getRoundId(),testRound.getRoundId());
    }
    /*@Test
    public void updateLatestRoundWinner_success(){
        
        Mockito.when(gameRoundRepository.findByRoundId(testRound.getRoundId())).thenReturn(testRound);
        Mockito.when(gameRepository.findByGameId(testRound.getCorrespondingGameId())).thenReturn(testGame);
        Mockito.when(cardRepository.findByCardId(testCard.getCardId())).thenReturn(testCard);
        gameService.updateLatestRoundWinner("testWinner",testRound.getRoundId(),testCard.getCardId());

    } */ //TODO move it to gameroundservice test








}
