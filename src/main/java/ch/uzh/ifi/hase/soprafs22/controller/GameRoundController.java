package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.GameRound;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameRoundGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameRoundPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameRoundService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameRoundController {

    private final GameRoundService gameRoundService;
    private final UserService userService;

    GameRoundController(GameRoundService gameRoundService, UserService userService) {
        this.gameRoundService = gameRoundService;
        this.userService = userService;
    }

    @PostMapping("/{gameRoundId}/white")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public GameRoundGetDTO playWhiteCard(@RequestHeader("Authorization") String token, @RequestBody GameRoundPostDTO gameRoundPostDTO,@PathVariable Long gameRoundId){
        userService.checkIfAuthorized(token);
        GameRound requestedGameRound=gameRoundService.playCard(gameRoundId,token,gameRoundPostDTO.getCardId());
        return DTOMapper.INSTANCE.convertEntityToGameRoundGetDTO(requestedGameRound);
    }

    @PostMapping("/{gameRoundId}/roundWinner")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void chooseRoundWinner(@RequestHeader("Authorization") String token, @RequestBody GameRoundPostDTO gameRoundPostDTO,@PathVariable Long gameRoundId){
        userService.checkIfAuthorized(token);
        gameRoundService.chooseRoundWinner(gameRoundId,token,gameRoundPostDTO.getCardId());
    }



}
