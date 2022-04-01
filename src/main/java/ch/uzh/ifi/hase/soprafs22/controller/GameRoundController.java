package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.service.GameRoundService;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameRoundController {

    private final GameRoundService gameRoundService;

    GameRoundController(GameRoundService gameRoundService) {
        this.gameRoundService = gameRoundService;
    }

}
