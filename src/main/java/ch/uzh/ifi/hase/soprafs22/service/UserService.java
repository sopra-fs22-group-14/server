package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  //getAllUsersFromRepo
  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(new Date());

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser); //saving in memory
    userRepository.flush(); //persist in Db

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

      String baseErrorMessage = "The username provided is not unique. Therefore, the user could not be created!";
      if (userByUsername != null) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage));
      }
  }

  public UserGetDTO login(UserPostDTO userPostDTO){
        String username=userPostDTO.getUsername();
        String password=userPostDTO.getPassword();
      if ((username == null || username.trim().isEmpty()) || (password == null || password.trim().isEmpty())) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have to specify both the username and password.");
      }

        User userByUsername=userRepository.findByUsername(username);
      if (userByUsername == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given username was not found.");
      }
      if (!userByUsername.getPassword().equals(password)) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You have entered an invalid password");
      }
      userByUsername.setStatus(UserStatus.ONLINE);
      userRepository.saveAndFlush(userByUsername);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userByUsername);

  }


    public void logout(String token) {
        User userByToken=userRepository.findByToken(token);
        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given token was not found.");
        }
        userByToken.setStatus(UserStatus.OFFLINE);
        userRepository.saveAndFlush(userByToken);
    }

    //needed for later
    public User getUserById(Long id){
        List<User> userList = getUsers();
        for (User user : userList) { //foreach to look through the UserList for the correct user
            if(user.getUserId().equals(id)){
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Non existing id. User was not found");
    }
    
    //public void makeUserOnline(User loggedInUser){loggedInUser.setStatus(true);}

    //public void makeUserOffline(User loggedOutUser){loggedOutUser.setStatus(false);}
}
