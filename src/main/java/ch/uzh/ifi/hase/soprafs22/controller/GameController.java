package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
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
        List<Game> games = gameService.getAllGames();
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();

        // convert each game to the API representation
        for (Game game : games)
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(game));
        // return DTOs
        return gameGetDTOs;

        /*
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
        */
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createNewGame(@RequestHeader("Authorization") String token,@RequestBody GamePostDTO gamePostDTO){
        userService.checkIfAuthorized(token);
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);
        Game newGame = gameService.createNewGame(gameInput,token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);

    }



//    // GAME DELETION
//    @DeleteMapping("/games/{gameId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteGame(@RequestHeader("Authorization") String token, @PathVariable Long gameId){
//        // checking if the user is authorized
//        userService.checkIfAuthorized(token);
//        gameService.deleteGameInWaitingArea(gameId, token); // game is deleted
//    }


    @PutMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO joinGame(@RequestHeader("Authorization") String token,@RequestBody GamePutDTO gamePutDTO){
        userService.checkIfAuthorized(token);
        Game joinedGame = gameService.joinGame(gamePutDTO.getGameId(),token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(joinedGame);
    }


    @GetMapping("/games/waitingArea/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO updatePlayerCount(@RequestHeader("Authorization") String token,
                                        @PathVariable Long gameId) {
        userService.checkIfAuthorized(token);
        Game requestedGame = gameService.updatePlayerCount(gameId, token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(requestedGame);
    }

    @PutMapping("/games/waitingArea/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void leaveWaitingArea(@RequestHeader("Authorization") String token,
                                        @PathVariable Long gameId) {
        userService.checkIfAuthorized(token);
        gameService.leaveWaitingArea(gameId, token);
    }


}
