package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

  // Using the REAL repositories
  @Qualifier("gameRepository")
  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private GameService gameService;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;

  @Qualifier("playerRepository")
  @Autowired
  private PlayerRepository playerRepository;

  private User testUser;
  private User testUser2;
  private Game testGame;

  @BeforeEach
  public void setup() {
    // add a user to the db (needed to create a new game)
    testUser = new User();
    testUser.setUsername("test1234user9876name0011");
    testUser.setPassword("testPassword");
    testUser.setToken("testToken");
    testUser = userService.createUser(testUser);

    Game testGame = new Game();
    testGame.setNumOfRounds(8);
    testGame.setGameEdition("regular");
    testGame.setGameName("testGame1234abcd5678efgh");
    testGame.setCardCzarMode(true);
    Game createdGame = gameService.createNewGame(testGame, testUser.getToken());
    Game foundGame = gameRepository.findByGameId(createdGame.getGameId());
    this.testGame = foundGame;
  }


  @Test
  public void test_createGame_integration() {

    assertNotNull(gameRepository.findByGameName("testGame1234abcd5678efgh"));

    // assertEquals(8, testGame.getNumOfRounds());   TODO change back after presentation
    assertEquals("regular", testGame.getGameEdition());
    assertEquals("testGame1234abcd5678efgh", testGame.getGameName());
    assertEquals(true, testGame.isCardCzarMode());

  }

  @Test
  public void test_joinGame_integration() {

    testUser2 = new User();
    testUser2.setUsername("test1234user9876name0012");
    testUser2.setPassword("testPassword");
    testUser2.setToken("testTokenn");
    testUser2 = userService.createUser(testUser2);

    assertNotNull(gameRepository.findByGameName("testGame1234abcd5678efgh"));

    Game joinedGame = gameService.joinGame(testGame.getGameId(), testUser2.getToken());
    Player joinedPlayer = playerRepository.findByPlayerId(testUser2.getUserId());
    assertEquals(joinedGame.getGameId(), joinedPlayer.getCurrentGameId());
    assertEquals(2, joinedGame.getNumOfPlayersJoined());

  }

  @Test
  public void test_leaveGameAndDelete_integration() {

    assertNotNull(gameRepository.findByGameName("testGame1234abcd5678efgh"));

    gameService.leaveGame(testGame.getGameId(), testUser.getToken());

    // check if player and game were deleted
    assertNull(gameRepository.findByGameId(testGame.getGameId()));
    assertNull(gameRepository.findByGameName("testGame1234abcd5678efgh"));
    assertNull(playerRepository.findByPlayerId(testUser.getUserId()));
    assertNotNull(userRepository.findByUserId(testUser.getUserId()));

    userRepository.delete(testUser);

    testUser = null;

  }

  @AfterEach
  public void cleanUp() {
    if (testGame != null) gameRepository.delete(testGame);
    if (testUser != null) {
      playerRepository.deleteById(testUser.getUserId());
      userRepository.delete(testUser);
    }
    if (testUser2 != null) {
      playerRepository.deleteById(testUser2.getUserId());
      userRepository.delete(testUser2);
    }
  }
}
