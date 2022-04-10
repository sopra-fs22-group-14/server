package ch.uzh.ifi.hase.soprafs22.controller;



import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
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
import static org.mockito.BDDMockito.given;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(GameController.class)

public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;
    @MockBean
    private UserService userService;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private GameRepository gameRepository;

    @InjectMocks
    private Player testPlayer;

    @InjectMocks
    private List<Player> players = new ArrayList<>();


    @Test
    void givenGames_whenGetGames_thenReturnJsonArray() throws Exception {        //  --------------------------------------------------->   GET "/users" test
        // given
        Game testGame=new Game();
        testGame.setGameId(1l);
        testGame.setGameName("abc");
        testGame.setNumOfPlayersJoined(1);
        List<Game> allGames = Collections.singletonList(testGame);

        given(gameService.getAllGames()).willReturn(allGames);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games").contentType(MediaType.APPLICATION_JSON). header("Authorization","currenttoken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].gameId", is(testGame.getGameId().intValue())))
                .andExpect(jsonPath("$[0].gameName", is(testGame.getGameName())));
        //.andExpect(jsonPath("$.numberOfPlayersJoined", is(testGame.getNumOfPlayersJoined())));

    }
    @Test
    void givenGame_whenCreateGame_thenReturn_GameDTO() throws Exception {

        Game testGame=new Game();
        testGame.setGameId(1L);
        testGame.setGameName("abc");
        //testGame.setNumOfPlayersJoined(1);
        testGame.setCardCzarMode(false);
        testGame.setNumOfRounds(16);
        testGame.setGameEdition("family");
        given(gameService.createNewGame(Mockito.any(),Mockito.anyString())).willReturn(testGame);

        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameName("abc");
        gamePostDTO.setGameEdition("family");
        gamePostDTO.setCardCzarMode(false);
        gamePostDTO.setNumOfRounds(16);


        // when
        MockHttpServletRequestBuilder postRequest = post("/games").contentType(MediaType.APPLICATION_JSON).content(asJsonString(gamePostDTO)). header("Authorization","currenttoken");    //convert it into a JSON


        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))                    // <-- Content-Type accepted?
                .andExpect(jsonPath("gameId", is(testGame.getGameId().intValue())));
    }
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }


}
