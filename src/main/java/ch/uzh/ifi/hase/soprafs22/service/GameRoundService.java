package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class GameRoundService {

    private Logger log = LoggerFactory.getLogger(GameRoundService.class);


    private final GameRoundRepository gameRoundRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public GameRoundService(@Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository, @Qualifier("gameRepository") GameRepository gameRepository, PlayerRepository playerRepository, @Qualifier("cardRepository") CardRepository cardRepository, DeckRepository deckRepository) {
        this.gameRoundRepository = gameRoundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }
    public GameRound createNewRound(Game game){
        if(game.getRoundIds().size()==game.getNumOfRounds()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game has finished! No more gameRounds.");
        }
        GameRound gameRound=new GameRound();
        //TODO how to select blackcard from repository and store cardCzar id
        Deck d=deckRepository.findByDeckId(game.getDeckID());
        List <Card> blackCards=cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(game.getDeckID(),false,false);
        Random rand = new Random(); //instance of random class
        int upperbound = blackCards.size();
        //generate random values from 0-to untill the blackcards size
        int int_random = rand.nextInt(upperbound);
        gameRound.setBlackCard(blackCards.get(int_random)); // add a random black card to game round
        blackCards.get(int_random).setPlayed(true); //this blackcard is played in the database
        cardRepository.saveAndFlush(blackCards.get(int_random));
        gameRound = gameRoundRepository.save(gameRound);
        gameRoundRepository.flush();

        game.setCurrentGameRoundIndex(game.getCurrentGameRoundIndex()+1); //increase the real gameround index by 1 in game
        game.setCurrentGameRoundId(gameRound.getRoundId()); // set the gameround id in the database
        List<Long> gameRoundIds=game.getRoundIds();
        gameRoundIds.add(gameRound.getRoundId());
        game.setRoundIds(gameRoundIds);

        gameRepository.saveAndFlush(game);
        return gameRound;
    }
    //TODO how to change old cardCzarsStatus
    public GameRound startNewRound(Game game){
        GameRound currentGameRound=createNewRound(game);
        long nextCardCzarId=computeCardCzarId(game);
        Player currentCardCzar=playerRepository.findByPlayerId(nextCardCzarId);
        currentCardCzar.setCardCzar(true);
        currentGameRound.setCardCzarId(currentCardCzar.getPlayerId());
        playerRepository.saveAndFlush(currentCardCzar);
        currentGameRound=gameRoundRepository.save(currentGameRound);
        gameRoundRepository.flush();

        return currentGameRound;
    }
    private long computeCardCzarId(Game game){
        int numberOfPlayers=game.getNumOfPlayersJoined();
        int currentGameRoundIndex=game.getCurrentGameRoundIndex();
        int nextCardCzarPos=(currentGameRoundIndex-1)%4;
        return game.getPlayerIds().get(nextCardCzarPos);

    }



    public List<GameRound> getAllGameRounds(){
        return this.gameRoundRepository.findAll();
    }
}
