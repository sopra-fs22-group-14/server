package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames() {
        // GET TOKEN FROM HEADER CHECK IF AUTHORIZED etc...
        Game gameA = new Game();
        gameA.setGameId(1L);
        gameA.setGameName("game1");
        gameA.setGameEdition("normal");
        gameA.setCardCzarMode(false);
        gameA.setNumOfPlayersJoined((int)((Math.random() * (4 - 1)) + 1));

        Game gameB = new Game();
        gameB.setGameId(2L);
        gameB.setGameName("game2");
        gameB.setGameEdition("weird");
        gameB.setCardCzarMode(true);
        gameB.setNumOfPlayersJoined((int)((Math.random() * (4 - 1)) + 1));

        List<GameGetDTO> gameGetDTOs = new ArrayList<>();
        // convert each user to the API representation
        gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(gameA));
        gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(gameB));

        // return DTOs
        return gameGetDTOs;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createNewGame(@RequestHeader("Authorization") String token,@RequestBody GamePostDTO gamePostDTO){
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);
        Game newGame = gameService.createNewGame(gameInput,token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);

    }






}
