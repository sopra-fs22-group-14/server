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
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class GameRoundService {

    private Logger log = LoggerFactory.getLogger(GameRoundService.class);
    private final static Random rand = new Random();


    private final GameRoundRepository gameRoundRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    public GameRoundService(@Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository, @Qualifier("gameRepository") GameRepository gameRepository, PlayerRepository playerRepository, @Qualifier("cardRepository") CardRepository cardRepository, DeckRepository deckRepository, UserRepository userRepository) {
        this.gameRoundRepository = gameRoundRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }
    public GameRound createNewRound(Game game){
        if(game.getRoundIds().size()==game.getNumOfRounds()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game has finished! No more gameRounds.");
        }
        GameRound gameRound=new GameRound();
        Deck d=deckRepository.findByDeckId(game.getDeckID());
        List <Card> blackCards=cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(game.getDeckID(),false,false);
        int upperbound = blackCards.size();
        //generate random values from 0-to untill the blackcards size
        int int_random = rand.nextInt(upperbound);
        gameRound.setBlackCard(blackCards.get(int_random)); // add a random black card to game round
        gameRound.setCorrespondingGameId(game.getGameId()); // and add the corresponding gameId
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


    public GameRound startNewRound(Game game){
        List<Long> players = game.getPlayerIds();
        for(Long playerId: players){
            Player player = playerRepository.findByPlayerId(playerId);
            player.setCardCzar(false);
            player.setHasPicked(false);
            while (player.getCardsOnHands().size()<10){
                List<Card> cards = player.getCardsOnHands();
                List<Card> remainingWhiteCards = cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(game.getDeckID(), true, false);
                int upperbound=remainingWhiteCards.size();
                int int_random = rand.nextInt(upperbound);
                cards.add(remainingWhiteCards.get(int_random));
                remainingWhiteCards.get(int_random).setPlayed(true);
                player.setCardsOnHands(cards);
                cardRepository.saveAndFlush(remainingWhiteCards.get(int_random));
            }
            playerRepository.saveAndFlush(player);
        }
        GameRound currentGameRound=createNewRound(game);
        if(game.isCardCzarMode()) {  //we don't need to compute card Czar Id if it is not cardCzarmode
            Long nextCardCzarId = computeCardCzarId(game);
            Player currentCardCzar = playerRepository.findByPlayerId(nextCardCzarId);
            currentCardCzar.setCardCzar(true);
            currentGameRound.setCardCzarId(currentCardCzar.getPlayerId());
            playerRepository.saveAndFlush(currentCardCzar);
        }
        currentGameRound.setNumberOfPicked(0);
        currentGameRound.setFinal(false);
        if(game.getRoundIds().size()==game.getNumOfRounds()){
            currentGameRound.setFinal(true);
        }
        currentGameRound=gameRoundRepository.save(currentGameRound);
        gameRoundRepository.flush();
        return currentGameRound;
    }
    private Long computeCardCzarId(Game game){
        int numberOfPlayers=game.getNumOfPlayersJoined();
        int currentGameRoundIndex=game.getCurrentGameRoundIndex();
        int nextCardCzarPos=(currentGameRoundIndex-1)%4;
        return game.getPlayerIds().get(nextCardCzarPos);
    }
    public GameRound playCard(Long gameRoundId,String token,Long cardId,Long gameId,String currentCombination){
        User userByToken=userRepository.findByToken(token);
        GameRound currentGameRound=gameRoundRepository.findByRoundId(gameRoundId);
        Game currentGame=gameRepository.findByGameId(gameId);
        Player currentPlayer=playerRepository.findByPlayerId(userByToken.getUserId());
        if(currentGame.isCardCzarMode()&& currentPlayer.isCardCzar()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "card czar can not play a card!");
        }

        if(currentGameRound.getCardAndPlayerIds().containsValue(userByToken.getUserId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "player already played a card!");
        }

        List<Card>currentCardsOnHand=currentPlayer.getCardsOnHands();
        Card playedCard=cardRepository.findByCardId(cardId);
        if(!currentCardsOnHand.contains(playedCard)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "player doesn't have this card!");
        }
        Map<Long,Long> currentCardAndPlayerIds=currentGameRound.getCardAndPlayerIds();
        currentCardAndPlayerIds.put(cardId,currentPlayer.getPlayerId());
        currentGameRound.setCardAndPlayerIds(currentCardAndPlayerIds);
        List<Card>currentPlayedCards=currentGameRound.getPlayedCards();
        currentPlayedCards.add(playedCard);
        currentGameRound.setPlayedCards(currentPlayedCards);
        currentCardsOnHand.remove(playedCard);
        currentPlayer.setCardsOnHands(currentCardsOnHand);
        List<String> currentPlayedCombinations=currentPlayer.getPlayedCombinations();
        currentPlayedCombinations.add(currentCombination);
        currentPlayer.setPlayedCombinations(currentPlayedCombinations);
        currentGameRound=gameRoundRepository.save(currentGameRound);
        gameRoundRepository.flush();
        playerRepository.saveAndFlush(currentPlayer);
        return currentGameRound;

    }

    public void pickWinner(Long gameId,Long gameRoundId,String token,Long cardId){
        Game requestedGame=gameRepository.findByGameId(gameId);
        if (requestedGame.isCardCzarMode()) {
            String roundWinner = chooseRoundWinner(gameRoundId, token, cardId);
            updateLatestRoundWinner(roundWinner, gameRoundId, cardId);
        } else {
            //if it is not cardCzar mode everybody can pick a card
            pickCard(gameRoundId,token,cardId);
        }
    }
    // updates the round winner name and the round winning card text in the game
    public void updateLatestRoundWinner(String roundWinner, Long gameRoundId, Long cardId) {
        // first, get the corresponding game from the gameRoundId
        GameRound gameRound = gameRoundRepository.findByRoundId(gameRoundId);
        Game game = gameRepository.findByGameId(gameRound.getCorrespondingGameId());
        Card winningCard = cardRepository.findByCardId(cardId);

        // set the two values used for the displaying of the winner
        game.setLatestRoundWinner(roundWinner);
        game.setLatestWinningCardText(winningCard.getCardText());
        gameRepository.save(game);
        gameRepository.flush();
    }
    public String chooseRoundWinner(Long gameRoundId,String token,Long cardId){

        User userByToken=userRepository.findByToken(token);
        Player currentPlayer=playerRepository.findByPlayerId(userByToken.getUserId());
        if(!currentPlayer.isCardCzar()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "player is not card Czar");
        }
        if (currentPlayer.isHasPicked()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "card czar already picked");
        }
        GameRound currentGameRound=gameRoundRepository.findByRoundId(gameRoundId);
        Long currentRoundWinnerId=currentGameRound.getCardAndPlayerIds().get(cardId);
        currentGameRound.setRoundWinnerId(currentRoundWinnerId);
        Player currentWinner=playerRepository.findByPlayerId(currentRoundWinnerId);
        int currentNumberOfPicked=currentWinner.getNumberOfPicked();
        currentWinner.setNumberOfPicked(currentNumberOfPicked+1);
        playerRepository.save(currentWinner);
        playerRepository.flush();
        String currentRoundWinnerName=currentWinner.getPlayerName();
        currentGameRound.setRoundWinnerName(currentRoundWinnerName);
        gameRoundRepository.saveAndFlush(currentGameRound);

        // after everything is done, start a new game round
        Game currentGame = gameRepository.findByGameId(currentGameRound.getCorrespondingGameId());
        if(!currentGameRound.isFinal()) {
            startNewRound(currentGame); //if it is last round don't start a new round
        }

        // and return the old round winner
        return currentRoundWinnerName;
    }

    public void pickCard(Long gameRoundId,String token,Long cardId){
        User userByToken=userRepository.findByToken(token);
        //TODO check if he picked before
        Player playerToPick=playerRepository.findByPlayerId(userByToken.getUserId());
        if(playerToPick.isHasPicked()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "player already picked");
        }
        GameRound currentGameRound=gameRoundRepository.findByRoundId(gameRoundId);
        Long currentPickedId=currentGameRound.getCardAndPlayerIds().get(cardId);
        Player currentPlayer=playerRepository.findByPlayerId(currentPickedId);
        int currentNumberOfPicked=currentPlayer.getNumberOfPicked();
        currentPlayer.setNumberOfPicked(currentNumberOfPicked+1);
        playerToPick.setHasPicked(true);
        playerRepository.save(currentPlayer);
        playerRepository.save(playerToPick);
        playerRepository.flush();
        int gameRoundNumberOfPicked=currentGameRound.getNumberOfPicked(); //how many players has played so far
        currentGameRound.setNumberOfPicked(gameRoundNumberOfPicked+1); //increase number of players ehich played by 1
        currentGameRound=gameRoundRepository.save(currentGameRound);
        gameRoundRepository.flush();
        if(currentGameRound.getNumberOfPicked()==4) {
            //if 4 player has picked a card then start a new round
            Game currentGame = gameRepository.findByGameId(currentGameRound.getCorrespondingGameId());
            // DIEGO: set latestWinningCardText for frontend detection of game ending
            currentGame.setLatestWinningCardText(currentGameRound.getRoundId()+"");
            gameRepository.save(currentGame);
            gameRepository.flush();
            if(!currentGameRound.isFinal()) { //if it is final round don't start a new round
                startNewRound(currentGame);
            }
        }
    }

    public List<GameRound> getAllGameRounds(){
        return this.gameRoundRepository.findAll();
    }
}
