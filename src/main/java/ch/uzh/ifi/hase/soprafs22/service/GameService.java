package ch.uzh.ifi.hase.soprafs22.service;

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

    public Game createNewGame(Game gameInput,Long userId) {
        if (gameRepository.findByGameName(gameInput.getGameName()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "GameName is already taken!");
        }

        Game game = new Game();
        game.setGameName(gameInput.getGameName());
        game.setCardCzarMode(gameInput.isCardCzarMode());
        game.setNumOfPlayersJoined(1);
        game.setGameEdition(gameInput.getGameEdition());
        // admin player is the one who creates game
        Player adminPlayer = createPlayer(userId);
        addPlayerToGame(adminPlayer,game);

        game = gameRepository.save(game);
        gameRepository.flush();
        //TODO maybe we can add the game id to the players and remove them from the lobby

        return game;
    }

        //TODO throw an error, if Player/User is already in a game, or if the token is expired/user logged out --> ask Szymon
    private void addPlayerToGame(Player playerToAdd, Game game){

        if (game.getNumOfPlayersJoined() < 5){
            List<Long> players = game.getPlayerIds();
            players.add(playerToAdd.getPlayerId());
            game.setPlayerIds(players);
            game.setNumOfPlayersJoined(game.getPlayerIds().size()); }

        else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game already full! Join another game."); }
    }


    //TODO we might add gameId to player entity and this function as well

    private Player createPlayer(Long userId){
        User user = userRepository.findByUserId(userId);
        Player player = new Player();
        player.setPlayerId(user.getUserId());
        player.setPlayerName(user.getUsername());
        player.setPlaying(false);
        player.setCardCzar(false);
        player.setRoundsWon(0);
        player = playerRepository.save(player);
        playerRepository.flush();
        return player;
    }


}
