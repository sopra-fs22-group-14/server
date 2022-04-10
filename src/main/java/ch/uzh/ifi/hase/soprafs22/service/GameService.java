package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@Transactional
public class GameService {

    private Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;

    //TODO I am not sure that we need autowired here. But probably we need since we have multiple repos

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("playerRepository")PlayerRepository playerRepository,@Qualifier("userRepository") UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
    }

    public List<Game> getAllGames(){
        return this.gameRepository.findAll();
    }

    // get a single game with a given Id
    public Game getGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with given Id doesn't exist!");
        }
        return game;
    }

    public Game createNewGame(Game gameInput,String token) {
        if (gameRepository.findByGameName(gameInput.getGameName()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "GameName is already taken!");
        }

        Game game = new Game();
        game.setGameName(gameInput.getGameName());
        game.setCardCzarMode(gameInput.isCardCzarMode());
        game.setNumOfPlayersJoined(1);
        game.setNumOfRounds(gameInput.getNumOfRounds());
        game.setGameEdition(gameInput.getGameEdition());
        // admin player is the one who creates game
        Player adminPlayer = createPlayer(token);

        addPlayerToGame(adminPlayer,game);

        game = gameRepository.saveAndFlush(game);

        //TODO maybe we can add the game id to the players and remove them from the lobby

        return game;
    }

        //TODO throw an error, if Player/User is already in a game, or if the token is expired/user logged out --> ask Szymon
    private void addPlayerToGame(Player playerToAdd, Game game){

        if (game.getNumOfPlayersJoined() < 4){
            List<Long> players = game.getPlayerIds();
            players.add(playerToAdd.getPlayerId());
            game.setPlayerIds(players);
            game.setNumOfPlayersJoined(game.getPlayerIds().size()); }

        else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game already full! Join another game."); }
    }


    private void removePlayerFromGame(Game gameToLeave, User userToRemove) {
        // TODO delete player object for corresponding user?
        // TODO delete game if there are no users left
        for (Long playerId : gameToLeave.getPlayerIds()) {
            if (playerId == userToRemove.getUserId()) {
                gameToLeave.getPlayerIds().remove(playerId);
                gameToLeave.decreasePlayers();
                return;
            }
        }
    }

    // function for joining a game
    public Game joinGame(Long gameId, String token){
        Game game = gameRepository.findByGameId(gameId);
        User userToJoin = userRepository.findByToken(token);
        if (userToJoin.getStatus() == UserStatus.ONLINE){
            if (!game.getPlayerIds().contains(userToJoin.getUserId())) {
                Player player = createPlayer(token);
                addPlayerToGame(player, game);
                return game;
            } else { throw new ResponseStatusException(HttpStatus.NO_CONTENT, "The user is already in the game!"); }
        } else { throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not logged in, cannot join a game!"); }
    }

    // method to update player count in the waiting area
    public Game updatePlayerCount(Long gameId, String token) {
        Game requestedGame = this.getGame(gameId); //throws error if not found
        User userWhoRequested = userRepository.findByToken(token);
        // if the requested user is NOT actually in the requested game, throw error
        if (!requestedGame.getPlayerIds().contains(userWhoRequested.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not in this game, join first!");
        }
        return requestedGame;
    }

    // method if a player decides to leave the waitingArea
    public void leaveWaitingArea(Long gameId, String token) {
        Game gameToLeave = gameRepository.findByGameId(gameId);
        User userToRemove = userRepository.findByToken(token);
        // if the user is not in the game, don't do anything (no error required)
        if (!gameToLeave.getPlayerIds().contains(userToRemove.getUserId()))
            return;
        removePlayerFromGame(gameToLeave, userToRemove);
    }


    //TODO we might add gameId to player entity and this function as well

    private Player createPlayer(String token){
        User user = userRepository.findByToken(token);
        Player player = new Player();
        player.setPlayerId(user.getUserId());
        player.setPlayerName(user.getUsername());
        player.setPlaying(true);
        player.setCardCzar(false);
        player.setRoundsWon(0);
        player = playerRepository.saveAndFlush(player);
        return player;
    }



}
