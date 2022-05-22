package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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
@Configuration
@EnableScheduling
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final GameService gameService;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository, GameService gameService, PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.gameService = gameService;
        this.playerRepository = playerRepository;
    }

    public void checkIfAuthorized(String token){
        User userByToken=userRepository.findByToken(token);
        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized!");
        }
    }

    public void checkIfTokenMatchesUserId(String token, Long userId){
        User userById = userRepository.findByUserId(userId);
        String userByIdToken = userById.getToken();
        if(!userByIdToken.equals(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized!");
        }
    }

    //getAllUsersFromRepo
    public List<User> getUsers() {
    return this.userRepository.findAll();
    }

    public User getUser(String token){
        User userByToken=userRepository.findByToken(token);
        if(userByToken==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist!");
        }
        return userByToken;
    }

    public User getUserRecords(Long userId){
        User userById = userRepository.findByUserId(userId);
        return userById;
    }

    public User createUser(User newUser) {
        String usernameToBeSet = newUser.getUsername();
        String passwordToBeSet=newUser.getPassword();
        if(usernameToBeSet==null ||usernameToBeSet.trim().isEmpty() || passwordToBeSet.trim().isEmpty() || passwordToBeSet==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have to specify both the username and password.");
        }
        String newToken = generateUniqueToken();
        newUser.setToken(newToken);
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreationDate(new Date());
        newUser.setTotalRoundWon(0);
        newUser.setTotalGameWon(0);
        newUser.setTimesPicked(0);
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

    public void changeUserProfile(String token, String username, Date birthday, String password){
        User requestedUser = userRepository.findByToken(token);
        String usernameFromDb = requestedUser.getUsername();
        if (!requestedUser.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The entered password is incorrect. Please enter your correct password");
        } else if ((username == null || username.trim().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have to specify the username.");
        }
        else if(userRepository.findByUsername(username)!=null) {
            if(!usernameFromDb.equals(username)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This username already exists. Please choose another username");
            }
        }
        requestedUser.setUsername(username);
        requestedUser.setBirthday(birthday);
        userRepository.saveAndFlush(requestedUser);
    }

    public User changeUserPassword(String token, String oldPassword, String newPassword){
        User requestedUser = userRepository.findByToken(token);
        if (!requestedUser.getPassword().equals(oldPassword)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The entered password is incorrect. Please enter your correct password");
        } else if ( (newPassword == null || newPassword.trim().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have to specify the password.");
        }
        requestedUser.setPassword(newPassword);
        String newToken=generateUniqueToken();
        requestedUser.setToken(newToken);
        userRepository.saveAndFlush(requestedUser);
        return requestedUser;
    }

    public void logout(String token) {
        User userByToken=userRepository.findByToken(token);
        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given token was not found.");
        }
        userByToken.setStatus(UserStatus.OFFLINE);
        // TODO test logout with randomToken
        String randomToken = generateUniqueToken();
        userByToken.setToken(randomToken);
        userRepository.saveAndFlush(userByToken);
        //return userByToken;
    }

    public void updateLastSeen(String token) {
        User user = userRepository.findByToken(token);
        user.setLastSeen(new Date());
    }

    public void updateLastGameRequest(String token) {
        User user = userRepository.findByToken(token);
        user.setLastGameRequest(new Date());
    }


    @Scheduled(fixedDelay = 10000)
    public void ensureAvailability() {
        long requestTimeout = 60000; // 60s
        long gameTimeout = 20000; // 20s
        for (User user : this.getUsers()) {
            if (user.getStatus() != UserStatus.ONLINE) return;
            long diffLastSeen = new Date().getTime() - user.getLastSeen().getTime();
            long diffLastGameRequest = new Date().getTime() - user.getLastGameRequest().getTime();
            Player player = playerRepository.findByPlayerId(user.getUserId());

            // User did not make any request in the given threshold
            if (diffLastSeen >= requestTimeout) {
                // if the user is currently in a game, kick him/her from that game
                if (player != null)
                    this.gameService.leaveGame(player.getCurrentGameId(), user.getToken());
                this.logout(user.getToken());   // and then, log him/her out

            } else if (player != null && diffLastGameRequest >= gameTimeout) {
                // if the user is in a game, ensure he didn't just go to another page
                this.gameService.leaveGame(player.getCurrentGameId(), user.getToken());
            }
        }
    }
}
