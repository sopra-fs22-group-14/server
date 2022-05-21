package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserProfileGetDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON)
            .header("Authorization","currenttoken");

      // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setUserId(1L);
    user.setUsername("testUsername");
    user.setPassword("Test Password");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("Test Password");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getUserId().intValue())))
        .andExpect(jsonPath("$.token", is(user.getToken())));
  }


    @Test
    public void loginValidUser() throws Exception {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUsername");
        user.setPassword("Test Password");
        user.setToken("1");
        //user.setStatus(UserStatus.ONLINE);

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setToken("1");
        userLoginDTO.setId(1L);

        given(userService.login(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userLoginDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getUserId().intValue())))
                .andExpect(jsonPath("$.token", is(user.getToken())));
    }

    @Test
    public void loginUserWithNoValues() throws Exception {
        // given
        User user = new User();
        user.setUsername("");
        user.setPassword("");

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setToken("");
        userLoginDTO.setId(1L);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userLoginDTO));

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have to specify both the username and password."))
                .when(userService).login(Mockito.any());

        // then
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }


    @Test
    public void getUserProfile() throws Exception{
      User user = new User();
      user.setUserId(1L);
      user.setUsername("testUsername");
      user.setToken("testToken");
      user.setBirthday(null);

      given(userService.getUser(Mockito.anyString())).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get(String.format("/users/%s", user.getUserId()))
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", "testToken");

      mockMvc.perform(getRequest).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.birthday", is(user.getBirthday())));
    }

    @Test
    public void getUserRecords() throws Exception{

      List<String> combinations = new ArrayList<>();
      combinations.add("abcd");
      combinations.add("efgh");

      User user = new User();
      user.setUserId(1L);
      user.setToken("testToken");
      user.setUsername("testUsername");
      user.setTotalRoundWon(10);
      user.setTimesPicked(8);
      user.setTotalGameWon(5);
      user.setBestCombinations(combinations);

      given(userService.getUserRecords(Mockito.any())).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get(String.format("/users/%s/records", user.getUserId()))
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", "testToken");

      mockMvc.perform(getRequest).andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.totalRoundWon", is(user.getTotalRoundWon())))
              .andExpect(jsonPath("$.timesPicked", is(user.getTimesPicked())))
              .andExpect(jsonPath("$.totalGameWon", is(user.getTotalGameWon())))
              .andExpect(jsonPath("$.bestCombinations", is(user.getBestCombinations())));

    }





  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}