package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
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

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setUserId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword");
    testUser.setToken("testToken");


    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
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
      testInput.setUsername("testUsername");
      testInput.setPassword("testPassword");
      Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      userService.login(testInput);

  }
  @Test
  public void changeUserProfile_success(){
      Date testBirthday=new Date();
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      userService.changeUserProfile("testToken","testUsername",testBirthday,"testPassword");
  }
  @Test
  public void logout_success(){
      testUser.setStatus(UserStatus.ONLINE);
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      userService.logout("testToken");
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
      User foundUser=userService.getUserRecords(testUser.getUserId());
      assertEquals(foundUser.getUserId(),testUser.getUserId());
  }
  @Test
  public void changeUserPassword_success(){
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      User foundUser=userService.changeUserPassword("testToken","testPassword","testNewPassword");
      assertEquals(foundUser.getUserId(),testUser.getUserId());
  }
  @Test
  public void checkIfAuthorized_success(){
      Mockito.when(userRepository.findByToken("testToken")).thenReturn(testUser);
      userService.checkIfAuthorized("testToken");
  }
  @Test
  public void checkIfTokenMatchesUserId_success(){
      Mockito.when(userRepository.findByUserId(testUser.getUserId())).thenReturn(testUser);
      userService.checkIfTokenMatchesUserId("testToken",testUser.getUserId());
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
