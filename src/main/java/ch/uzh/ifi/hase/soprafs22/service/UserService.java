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



    /** INITIAL SKETCH FOR CHECKING AUTHORIZATION */
    public void checkIfAuthorized(String token){
        User userByToken=userRepository.findByToken(token);
        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not Authorized!");
        }
    }


    //getAllUsersFromRepo
    public List<User> getUsers() {
    return this.userRepository.findAll();
    }


    /** PRODUCTION READY*/
    /** SZYMON */
    public User createUser(User newUser) {
        String newToken = generateUniqueToken();
        newUser.setToken(newToken);
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

    // Helper needed for the login / registration to check if the assigned token is unique
    /** PRODUCTION READY*/
    /** SZYMON */
    public String generateUniqueToken(){
        String newToken = UUID.randomUUID().toString();
        User userByToken = userRepository.findByToken(newToken);
        while(userByToken != null){
            // meaning there is already user with this token
            newToken = UUID.randomUUID().toString();
        }
        return newToken;
    }



    private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        String baseErrorMessage = "The username provided is not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage));
        }
    }

    public User login(User userInput){
        String username=userInput.getUsername();
        String password=userInput.getPassword();
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
        String newToken = generateUniqueToken();
        userByUsername.setToken(newToken);
        userRepository.saveAndFlush(userByUsername);
        return userByUsername;

    }


    public User logout(String token) {
        User userByToken=userRepository.findByToken(token);
        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given token was not found.");
        }
        userByToken.setStatus(UserStatus.OFFLINE);
        //userByToken.setToken("");
        userRepository.saveAndFlush(userByToken);
        return userByToken;
    }

    //needed for later --- why just not findUserById from repository?
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
