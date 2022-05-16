package ch.uzh.ifi.hase.soprafs22.controller;



import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.service.GameRoundService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(GameController.class)

public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private GameRoundService gameRoundService;

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
    void givenGames_whenGetJoinableGames_thenReturnJsonArray() throws Exception {        //  --------------------------------------------------->   GET "/users" test
        // given
        Game testGame=new Game();
        testGame.setGameId(1l);
        testGame.setGameName("abc");
        testGame.setNumOfPlayersJoined(1);
        List<Game> joinableGames = Collections.singletonList(testGame);

        given(gameService.joinableGames()).willReturn(joinableGames);

        // when
            MockHttpServletRequestBuilder getRequest = get("/games").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization","currenttoken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].gameId", is(testGame.getGameId().intValue())))
                .andExpect(jsonPath("$[0].gameName", is(testGame.getGameName())))
                .andExpect(jsonPath("$[0].numOfPlayersJoined", is(testGame.getNumOfPlayersJoined())));

    }
    @Test
    void givenGame_whenCreateGame_thenReturn_GameGetDTO() throws Exception {


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
    @Test
    void givenGame_whenJoinGame_thenReturn_GameGetDTO() throws Exception{

        Game testGame=new Game();
        testGame.setGameId(1L);
        testGame.setGameName("abc");
        testGame.setNumOfPlayersJoined(2);
        testGame.setCardCzarMode(false);
        testGame.setNumOfRounds(16);
        testGame.setGameEdition("family");

        given(gameService.joinGame(Mockito.any(),Mockito.anyString())).willReturn(testGame);

        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setGameId(1L);

        MockHttpServletRequestBuilder putRequest = put("/games").contentType(MediaType.APPLICATION_JSON).content(asJsonString(gamePutDTO)). header("Authorization","currenttoken");

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("gameId", is(testGame.getGameId().intValue())))
                .andExpect(jsonPath("gameName", is(testGame.getGameName())))
                .andExpect(jsonPath("numOfPlayersJoined", is(testGame.getNumOfPlayersJoined())))

        ;
    }
    @Test
    void givenGames_whenGetPlayer_thenReturnJsonArray() throws Exception{
        Player testPlayer=new Player();
        testPlayer.setPlayerId(1L);
        testPlayer.setPlaying(true);
        testPlayer.setCardCzar(true);
        testPlayer.setRoundsWon(0);
        testPlayer.setPlayerName("abc");
        given(gameService.getPlayer(Mockito.anyString())).willReturn(testPlayer);
        MockHttpServletRequestBuilder getRequest = get("/player").contentType(MediaType.APPLICATION_JSON). header("Authorization","currenttoken");
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.playerId", is(testPlayer.getPlayerId().intValue())))
                .andExpect(jsonPath("$.playerName", is(testPlayer.getPlayerName())))
                .andExpect(jsonPath("$.roundsWon", is(testPlayer.getRoundsWon())));


    }
    @Test
    void givenGames_whenGetGameRound_thenReturnJsonArray() throws Exception{
        GameRound testRound=new GameRound();
        testRound.setRoundId(1L);
        testRound.setCardCzarId(1L);
        Game testGame=new Game();
        testGame.setGameId(2L);
        testGame.setGameName("abc");
        testGame.setNumOfPlayersJoined(2);
        testGame.setCardCzarMode(false);
        testGame.setNumOfRounds(16);
        testGame.setGameEdition("family");
        given(gameService.getGameRound(anyLong())).willReturn(testRound);
        given(gameService.getGame(anyLong())).willReturn(testGame);
        MockHttpServletRequestBuilder getRequest = get(String.format("/%s/gameround", testGame.getGameId())).contentType(MediaType.APPLICATION_JSON). header("Authorization","currenttoken");
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roundId", is(testRound.getRoundId().intValue())))
                .andExpect(jsonPath("$.cardCzarId", is(testRound.getCardCzarId().intValue())));

    }
    @Test
    void givenGame_whenLeaveGame_return_Ok() throws Exception {
        Game testGame=new Game();
        testGame.setGameId(1L);
        testGame.setGameName("abc");
        testGame.setNumOfPlayersJoined(2);
        testGame.setCardCzarMode(false);
        testGame.setNumOfRounds(16);
        testGame.setGameEdition("family");
        List<Long> playerIds = new ArrayList<>();
        playerIds.add(1L);
        testGame.setPlayerIds(playerIds);

        Player testPlayer=new Player();
        testPlayer.setPlayerId(1L);
        testPlayer.setPlaying(true);
        testPlayer.setCardCzar(true);
        testPlayer.setRoundsWon(0);
        testPlayer.setPlayerName("abc");

        given(gameService.getGame(1L)).willReturn(testGame);
        given(playerRepository.findByPlayerId(3L)).willReturn(testPlayer);

        doNothing().when(gameService).leaveGame(anyLong(),Mockito.anyString());
        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setGameId(1L);

        MockHttpServletRequestBuilder putRequest = put(String.format("/leave/%s", testGame.getGameId())).contentType(MediaType.APPLICATION_JSON). header("Authorization","currenttoken");
        mockMvc.perform(putRequest).andExpect(status().isOk())
        ;
    }

    @Test
    void givenGame_whenUpdateCount_return_Ok() throws Exception{

        Game testGame=new Game();
        testGame.setGameId(1L);
        testGame.setGameName("abc");
        testGame.setNumOfPlayersJoined(2);
        testGame.setCardCzarMode(false);
        testGame.setNumOfRounds(16);
        testGame.setGameEdition("family");
        List<Long> playerIds = new ArrayList<>();
        playerIds.add(1L);
        testGame.setPlayerIds(playerIds);

        Player testPlayer=new Player();
        testPlayer.setPlayerId(1L);
        testPlayer.setPlaying(true);
        testPlayer.setCardCzar(true);
        testPlayer.setRoundsWon(0);
        testPlayer.setPlayerName("abc");
        given(gameService.updatePlayerCount(anyLong(),Mockito.anyString())).willReturn(testGame);
        MockHttpServletRequestBuilder getRequest = get(String.format("/games/waitingArea/%s", testGame.getGameId())).contentType(MediaType.APPLICATION_JSON). header("Authorization","currenttoken");
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gameId", is(testGame.getGameId().intValue())))
                .andExpect(jsonPath("$.gameName", is(testGame.getGameName())))
                .andExpect(jsonPath("$.numOfPlayersJoined", is(testGame.getNumOfPlayersJoined())));

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
