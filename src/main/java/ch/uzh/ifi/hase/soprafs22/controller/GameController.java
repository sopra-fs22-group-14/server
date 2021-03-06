package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs22.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public List<GameGetDTO> joinableGames(@RequestHeader("Authorization") String token) {

        // GET TOKEN FROM HEADER CHECK IF AUTHORIZED etc..
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        List<Game> games = gameService.joinableGames();
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();

        // convert each game to the API representation
        for (Game game : games)
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(game));
        // return DTOs
        return gameGetDTOs;

    }

    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@RequestHeader("Authorization") String token, @PathVariable long gameId) {
        userService.checkIfAuthorized(token);
        Game requestedGame = gameService.getGame(gameId);
        User userByToken=userService.getUser(token);
        List<String> currentPlayerNames=requestedGame.getPlayerNames();
        currentPlayerNames.remove(userByToken.getUsername());
        requestedGame.setPlayerNames(currentPlayerNames);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(requestedGame);
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createNewGame(@RequestHeader("Authorization") String token,@RequestBody GamePostDTO gamePostDTO){
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOToEntity(gamePostDTO);
        Game newGame = gameService.createNewGame(gameInput,token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);
    }

    @PostMapping("/combinations")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void saveCombination(@RequestHeader("Authorization") String token,@RequestBody CombinationPostDTO combinationPostDTO){
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.checkIfTokenMatchesUserId(token, combinationPostDTO.getPlayerId());
        userService.updateLastGameRequest(token);
        gameService.saveBestCombination(combinationPostDTO.getBestCombination(), combinationPostDTO.getPlayerId());
    }


    @PutMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO joinGame(@RequestHeader("Authorization") String token,@RequestBody GamePutDTO gamePutDTO) {
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        Game joinedGame = gameService.joinGame(gamePutDTO.getGameId(),token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(joinedGame);
    }


    @GetMapping("/games/waitingArea/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO updatePlayerCount(@RequestHeader("Authorization") String token,
                                        @PathVariable Long gameId) {
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.updateLastGameRequest(token);
        Game requestedGame = gameService.updatePlayerCount(gameId, token);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(requestedGame);
    }

    @GetMapping("/{gameId}/gameEnd")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameSummaryGetDTO getGameSummaryAndWinner(@RequestHeader("Authorization") String token,
                                        @PathVariable Long gameId) {
        userService.checkIfAuthorized(token);
        Game requestedGame = gameService.getGameSummaryAndWinner(gameId);
        return DTOMapper.INSTANCE.convertEntityToGameSummaryGetDTO(requestedGame);
    }

    @PutMapping("/leave/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void leaveWaitingArea(@RequestHeader("Authorization") String token,
                                        @PathVariable Long gameId) {
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        gameService.leaveGame(gameId, token);
    }



    @GetMapping("/player")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO getPlayer(@RequestHeader("Authorization") String token) {
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.updateLastGameRequest(token);
        Player requestedPlayer=gameService.getPlayer(token);
        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(requestedPlayer);
    }


    //need to test this controller
    @GetMapping("/{gameId}/gameround")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameRoundGetDTO getRound(@RequestHeader("Authorization") String token,@PathVariable long gameId) {
        userService.checkIfAuthorized(token);
        userService.updateLastSeen(token);
        userService.updateLastGameRequest(token);
        GameRound requestedGameRound=gameService.getGameRound(gameId);
        Game gameById=gameService.getGame(gameId);
        if(!gameById.isCardCzarMode()){
            List<Card> currentPlayedCards=requestedGameRound.getPlayedCards();
            Player currentPlayer=gameService.getPlayer(token);
            if(!currentPlayedCards.isEmpty()){
                Map<Long,Long> currentCardAndPlayerIds=requestedGameRound.getCardAndPlayerIds();
                for(int i=0; i<currentPlayedCards.size(); i++ ){
                    Long currentCardId=currentPlayedCards.get(i).getCardId();
                    if(currentCardAndPlayerIds.get(currentCardId).equals(currentPlayer.getPlayerId())){
                        Card cardToChange=gameService.getCard(currentCardId);
                        cardToChange.setCanBeChoosen(false);
                        currentPlayedCards.set(i,cardToChange);
                        requestedGameRound.setPlayedCards(currentPlayedCards);
                    }
                }

            }
        }
        return DTOMapper.INSTANCE.convertEntityToGameRoundGetDTO(requestedGameRound);
    }

}
