package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.entity.UserTimestamp;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserTimestampRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserTimestampRepository userTimestampRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private UserTimestamp testUserTimestamp;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setUserId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword");
    testUser.setToken("testToken");

    testUserTimestamp = new UserTimestamp();

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    Mockito.when(userTimestampRepository.save(Mockito.any())).thenReturn(testUserTimestamp);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);


    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getUserId(), createdUser.getUserId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void login_success(){
      testUser.setToken("testToken");
      User testInput=new User();
      UserStatus initialStatus = testInput.getStatus();
      testInput.setUsername("testUsername");
      testInput.setPassword("testPassword");
      Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

      User loggedInUser = userService.login(testInput);

      assertEquals(initialStatus, null);
      assertEquals(loggedInUser.getStatus(), UserStatus.ONLINE);
  }
  @Test
  public void changeUserProfile_success(){
      Date testBirthday=new Date();
      String initialUsername = testUser.getUsername();
      Date initialBirthday = testUser.getBirthday();
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      userService.changeUserProfile("testToken","newTestUsername",testBirthday,"testPassword");

      assertNotEquals(initialBirthday, testUser.getBirthday());
      assertNotEquals(initialUsername, testUser.getUsername());
      assertEquals(testUser.getUsername(), "newTestUsername");
      assertEquals(testUser.getBirthday(), testBirthday);
  }
  @Test
  public void logout_success(){
      testUser.setStatus(UserStatus.ONLINE);
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      userService.logout("testToken");
      assertEquals(testUser.getStatus(), UserStatus.OFFLINE);
  }
  @Test
  public void getUser_success(){
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      User foundUser=userService.getUser("testToken");
      assertEquals(foundUser.getUserId(),testUser.getUserId());
  }
  @Test
  public void getUserRecords_success(){
      Mockito.when(userRepository.findByUserId(testUser.getUserId())).thenReturn(testUser);
      assertDoesNotThrow(() -> userService.getUserRecords(testUser.getUserId()));
  }
  @Test
  public void changeUserPassword_success(){
      String initialPassword = testUser.getPassword();
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      User foundUser=userService.changeUserPassword("testToken","testPassword","testNewPassword");

      assertEquals(foundUser.getPassword(), "testNewPassword");
      assertNotEquals(foundUser.getPassword(), initialPassword);
  }
  @Test
  public void checkIfAuthorized_success(){
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      assertDoesNotThrow(() -> userService.checkIfAuthorized("testToken"));
  }
  @Test
  public void checkIfTokenMatchesUserId_success(){
      Mockito.when(userRepository.findByUserId(testUser.getUserId())).thenReturn(testUser);
      assertDoesNotThrow(() -> userService.checkIfTokenMatchesUserId("testToken",testUser.getUserId()));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void test_updateLastSeen() {

    User testUser = new User();
    String testToken = "123abc";
    UserTimestamp testUserTimeStamp = new UserTimestamp();

    Date currentLastSeen = testUserTimeStamp.getLastSeen();

    Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
    Mockito.when(userTimestampRepository.findByUserId(Mockito.any())).thenReturn(testUserTimeStamp);

    userService.updateLastSeen(testToken);
    assertNotEquals(testUserTimeStamp.getLastSeen(), currentLastSeen);

  }

  @Test
  public void test_updateLastGameRequest() {

    User testUser = new User();
    String testToken = "123abc";
    UserTimestamp testUserTimeStamp = new UserTimestamp();

    Date currentLastGameRequest = testUserTimeStamp.getLastSeen();

    Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
    Mockito.when(userTimestampRepository.findByUserId(Mockito.any())).thenReturn(testUserTimeStamp);

    userService.updateLastGameRequest(testToken);
    // check whether the timestamp was successfully updated
    assertNotEquals(testUserTimeStamp.getLastGameRequest(), currentLastGameRequest);

  }

//    @Test
//    public void checkifAuthorized_success() {
//        // when -> any object is being save in the userRepository -> return the dummy
//        // testUser
//        Mockito.when(userRepository.findByUserId(1L)).thenReturn(testUser);
//        userService.checkIfAuthorized(testUser.getToken());
//    }


//    @Test
//    public void checkIfAuthorized_fail_throwException() {
//        // given -> a first user has already been created
//        userService.checkIfAuthorized("abcd");
//
//        // when -> setup additional mocks for UserRepository
//        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
//
//        // then -> attempt to create second user with same user -> check that an error
//        // is thrown
//        assertThrows(ResponseStatusException.class, () -> userService.checkIfAuthorized(testUser.getToken()));
//    }

}
