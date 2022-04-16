package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.CardRepository;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.GameRoundRepository;
import com.sun.xml.bind.v2.TODO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class GameRoundService {

    private Logger log = LoggerFactory.getLogger(GameRoundService.class);


    private GameRoundRepository gameRoundRepository;
    private final GameRepository gameRepository;
    private final CardRepository cardRepository;

    public GameRoundService(@Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository,@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("cardRepository") CardRepository cardRepository) {
        this.gameRoundRepository = gameRoundRepository;
        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
    }
    public GameRound createNewRound(Game game){
        if(game.getRoundIds().size()==game.getNumOfRounds()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game has finished! No more gameRounds.");
        }
        GameRound gameRound=new GameRound();
        //TODO how to select blackcard from repository and store cardCzar id

        gameRound = gameRoundRepository.save(gameRound);
        gameRoundRepository.flush();
        return gameRound;
    }




    public List<GameRound> getAllGameRounds(){
        return this.gameRoundRepository.findAll();
    }
}
