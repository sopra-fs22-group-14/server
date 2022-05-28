package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.controller.UserController;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.entity.UserTimestamp;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserTimestampRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  private User testUser;

  /**
  In our case, deleting the entire database does not make sense, because we do not use H2, but we use a
   persistent database. What we do here is create a dummy user with a highly unlikely username, do some
   tests and delete this user again in the end.
   */

  @BeforeEach
  public void setup() {
    testUser = new User();
    testUser.setUsername("hjsffgydvsdcfsd2234453sdf");
    testUser.setPassword("testPassword");
    testUser.setTimesPicked(0);
    testUser = userService.createUser(testUser);
  }

  @Test
  public void createUser_validInputs_success() {

    // given
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));

    // then
    assertEquals(testUser.getUserId(), testUser.getUserId());
    assertEquals(testUser.getUsername(), testUser.getUsername());
    assertEquals(testUser.getPassword(), testUser.getPassword());
    assertEquals(testUser.getTimesPicked(), testUser.getTimesPicked());
    assertNotNull(testUser.getToken());
    assertEquals(UserStatus.ONLINE, testUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setUsername("hjsffgydvsdcfsd2234453sdf");
    testUser2.setPassword("testPassword");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }

  @Test
  public void login_success() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));

    userService.login(testUser);
    assertEquals(UserStatus.ONLINE, userRepository.findByUserId(testUser.getUserId()).getStatus());
  }

  @Test
  public void logout_success() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));

    userService.logout(testUser.getToken());
    assertEquals(UserStatus.OFFLINE, userRepository.findByUserId(testUser.getUserId()).getStatus());
  }

  @Test
  public void checkIfAuthorized_success() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));
    assertDoesNotThrow(() -> userService.checkIfAuthorized(testUser.getToken()));
  }

  @Test
  public void checkIfAuthorized_failure() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));
    assertThrows(ResponseStatusException.class, () -> userService.checkIfAuthorized("wrongToken"));
  }

  @Test
  public void getUser() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));
    User foundUser = userService.getUser(testUser.getToken());
    assertEquals(foundUser.getUserId(), testUser.getUserId());
    assertEquals(foundUser.getToken(), testUser.getToken());
  }

  @Test
  public void changeUserProfile_success() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));

    Date newBirthday = new Date();
    String newUsername = "aaaqqqgggbbbtttwwwkkklll";

    userService.changeUserProfile(testUser.getToken(), newUsername, newBirthday, testUser.getPassword());

    User db_user = userRepository.findByUserId(testUser.getUserId());
    assertEquals(newUsername, db_user.getUsername());
  }

  @Test
  public void changeUserProfile_failure() {
    assertNotNull(userRepository.findByUsername("hjsffgydvsdcfsd2234453sdf"));

    Date newBirthday = new Date();
    String newUsername = "aaaqqqgggbbbtttwwwkkklll";

    assertThrows(ResponseStatusException.class,
            () -> userService.changeUserProfile(testUser.getToken(), newUsername, newBirthday, "wrongPassword"));
  }

  @AfterEach
  public void cleanUp() {
    if (testUser != null) userRepository.deleteById(testUser.getUserId());
  }

}
