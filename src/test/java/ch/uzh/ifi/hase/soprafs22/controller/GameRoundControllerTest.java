package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Card;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.GameRound;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameRoundController.class)
public class GameRoundControllerTest {
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

    @Test
    public void chooseRoundWinner() throws Exception{
        Game testGame=new Game();
        testGame.setGameId(1L);
        testGame.setGameName("abc");
        //testGame.setNumOfPlayersJoined(1);
        testGame.setCardCzarMode(true);
        testGame.setNumOfRounds(16);
        testGame.setGameEdition("family");

        GameRound gameRound=new GameRound();

        gameRound.setRoundId(1L);
        gameRound.setCardCzarId(3L);

        Card testCard=new Card();
        testCard.setCardId(2L);
        testCard.setPlayed(true);
        testCard.setDeckId(6L);
        testCard.setWhite(false);
        testCard.setGameEdition("family");
        testCard.setCardText("blacktest");

        Card testCard2=new Card();
        testCard2.setCardId(3L);
        testCard2.setPlayed(true);
        testCard2.setDeckId(6L);
        testCard2.setWhite(true);
        testCard2.setGameEdition("family");
        testCard2.setCardText("whitetest");

        Card testCard3=new Card();
        testCard3.setCardId(4L);
        testCard3.setPlayed(true);
        testCard3.setDeckId(6L);
        testCard3.setWhite(true);
        testCard3.setGameEdition("family");
        testCard3.setCardText("whitetest");

        Card testCard4=new Card();
        testCard4.setCardId(5L);
        testCard4.setPlayed(true);
        testCard4.setDeckId(6L);
        testCard4.setWhite(true);
        testCard4.setGameEdition("family");
        testCard4.setCardText("whitetest");

        gameRound.setBlackCard(testCard);

        List<Card> testPlayedCards=new ArrayList<>();
        testPlayedCards.add(testCard2);
        testPlayedCards.add(testCard3);
        testPlayedCards.add(testCard4);
        gameRound.setPlayedCards(testPlayedCards);
        Map<Long,Long> testCardAndPlayerIds=new HashMap<>();
        testCardAndPlayerIds.put(3L,6L);
        testCardAndPlayerIds.put(4L,7L);
        testCardAndPlayerIds.put(5L,8L);
        gameRound.setCardAndPlayerIds(testCardAndPlayerIds);


       doNothing().when(gameRoundService).pickWinner(anyLong(),anyLong(),Mockito.anyString(),anyLong());
        GameRoundPostDTO gameRoundPostDTO = new GameRoundPostDTO();
        gameRoundPostDTO.setCardId(3L);
        MockHttpServletRequestBuilder postRequest = post(String.format("/%s/roundWinner", gameRound.getRoundId())).contentType(MediaType.APPLICATION_JSON). header("Authorization","currenttoken").content(asJsonString(gameRoundPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isNoContent());
    }
    @Test
    public void playWhiteCard() throws Exception{
        GameRound gameRound=new GameRound();

        gameRound.setRoundId(1L);
        gameRound.setCardCzarId(3L);

        Card testCard=new Card();
        testCard.setCardId(2L);
        testCard.setPlayed(true);
        testCard.setDeckId(6L);
        testCard.setWhite(false);
        testCard.setGameEdition("family");
        testCard.setCardText("blacktest");

        Card testCard2=new Card();
        testCard2.setCardId(3L);
        testCard2.setPlayed(true);
        testCard2.setDeckId(6L);
        testCard2.setWhite(true);
        testCard2.setGameEdition("family");
        testCard2.setCardText("whitetest");

        Card testCard3=new Card();
        testCard3.setCardId(4L);
        testCard3.setPlayed(true);
        testCard3.setDeckId(6L);
        testCard3.setWhite(true);
        testCard3.setGameEdition("family");
        testCard3.setCardText("whitetest");

        gameRound.setBlackCard(testCard);

        List<Card> testPlayedCards=new ArrayList<>();
        testPlayedCards.add(testCard2);
        testPlayedCards.add(testCard3);
        gameRound.setPlayedCards(testPlayedCards);
        Map<Long,Long> testCardAndPlayerIds=new HashMap<>();
        testCardAndPlayerIds.put(3L,6L);
        testCardAndPlayerIds.put(4L,7L);
        gameRound.setCardAndPlayerIds(testCardAndPlayerIds);
        given(gameRoundService.playCard(anyLong(),Mockito.anyString(),anyLong(),anyLong())).willReturn(gameRound);
        GameRoundPostDTO gameRoundPostDTO = new GameRoundPostDTO();
        gameRoundPostDTO.setCardId(4L);
        MockHttpServletRequestBuilder postRequest = post(String.format("/%s/white", gameRound.getRoundId())).contentType(MediaType.APPLICATION_JSON). header("Authorization","currenttoken").content(asJsonString(gameRoundPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isNoContent());

    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }


}
