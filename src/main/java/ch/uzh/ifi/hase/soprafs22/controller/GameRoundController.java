package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.GameRound;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameRoundGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameRoundPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameRoundService;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameRoundController {

    private final GameRoundService gameRoundService;
    private final UserService userService;
    private final GameService gameService;

    GameRoundController(GameRoundService gameRoundService, UserService userService, GameService gameService) {
        this.gameRoundService = gameRoundService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @PostMapping("/{gameRoundId}/white")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public GameRoundGetDTO playWhiteCard(@RequestHeader("Authorization") String token, @RequestBody GameRoundPostDTO gameRoundPostDTO,@PathVariable Long gameRoundId){
        userService.checkIfAuthorized(token);
        gameService.isInGame(token,gameRoundPostDTO.getGameId());
        GameRound requestedGameRound=gameRoundService.playCard(gameRoundId,token,gameRoundPostDTO.getCardId(),gameRoundPostDTO.getGameId(),gameRoundPostDTO.getCurrentCombination());
        return DTOMapper.INSTANCE.convertEntityToGameRoundGetDTO(requestedGameRound);
    }

    @PostMapping("/{gameRoundId}/roundWinner")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void chooseRoundWinner(@RequestHeader("Authorization") String token, @RequestBody GameRoundPostDTO gameRoundPostDTO,@PathVariable Long gameRoundId){
        userService.checkIfAuthorized(token);
        gameService.isInGame(token,gameRoundPostDTO.getGameId());
        gameRoundService.pickWinner(gameRoundPostDTO.getGameId(),gameRoundId,token,gameRoundPostDTO.getCardId());
    }


}
