package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
    this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader("Authorization") String token) {
    // fetch all users in the internal representation
    userService.checkIfAuthorized(token);
    userService.updateLastSeen(token);
    List<User> users = userService.getUsers();
    User requestedUser = userService.getUser(token);
    users.remove(requestedUser);
    List<UserGetDTO> userGetDTOs = new ArrayList<>();
    // convert each user to the API representation
    for (User user : users) {
        userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
        return userGetDTOs;
    }

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserLoginDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User createdUser = userService.createUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(createdUser); //LoginDTO
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserLoginDTO login(@RequestBody UserPostDTO userPostDTO) {
        User userInput=DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User loggedInUser=userService.login(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(loggedInUser);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserProfileGetDTO getUserProfile(@RequestHeader("Authorization") String token, @PathVariable long userId){
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.checkIfTokenMatchesUserId(token, userId);
        User requestedUser = userService.getUser(token);
        return DTOMapper.INSTANCE.convertEntityToUserProfileGetDTO(requestedUser);
    }

    @GetMapping("/users/{userId}/records")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserRecordsGetDTO getUserRecords(@RequestHeader("Authorization") String token, @PathVariable long userId){
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        User requestedUser = userService.getUserRecords(userId);
        return DTOMapper.INSTANCE.convertEntityToUserRecordsGetDTO(requestedUser);
    }

    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void changeUserProfile(@RequestHeader("Authorization") String token , @PathVariable long userId, @RequestBody UserProfilePutDTO userProfilePutDTO){
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.checkIfTokenMatchesUserId(token, userId);
        userService.changeUserProfile(token, userProfilePutDTO.getUsername(), userProfilePutDTO.getBirthday(), userProfilePutDTO.getPassword());
    }

    @PutMapping("/users/{userId}/password")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPasswordGetDTO changeUserPassword(@RequestHeader("Authorization") String token, @PathVariable long userId, @RequestBody UserPasswordPutDTO userPasswordPutDTO){
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.checkIfTokenMatchesUserId(token, userId);
        User changedUser = userService.changeUserPassword(token, userPasswordPutDTO.getOldPassword(), userPasswordPutDTO.getNewPassword());
        return DTOMapper.INSTANCE .convertEntityToUserPasswordGetDTO(changedUser);
    }

    @PostMapping("users/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void logout(@RequestHeader("Authorization") String token) {
        //System.out.println("TOKEN: "+token);
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.logout(token);
    }

}
