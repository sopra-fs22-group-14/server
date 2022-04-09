package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs22.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames(@RequestHeader("Authorization") String token) {
        // GET TOKEN FROM HEADER CHECK IF AUTHORIZED etc..
        userService.checkIfAuthorized(token);
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

    @PutMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO joinGame(@RequestHeader("Authorization") String token,@RequestBody GamePutDTO gamePutDTO){

        Game joinedGame = gameService.joinGame(gamePutDTO.getGameId(),token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(joinedGame);

    }
    







}
