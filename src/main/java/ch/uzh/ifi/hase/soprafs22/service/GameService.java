package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.CardColor;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

@Service
@Transactional
public class GameService {

    private Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final GameRoundRepository gameRoundRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final GameRoundService gameRoundService;
    private final CardRepository cardRepository;


    private final DeckRepository deckRepository;

    //TODO I am not sure that we need autowired here. But probably we need since we have multiple repos

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("userRepository") UserRepository userRepository, GameRoundService gameRoundService, CardRepository cardRepository, DeckRepository deckRepository) {
        this.gameRepository = gameRepository;
        this.gameRoundRepository = gameRoundRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.gameRoundService = gameRoundService;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public List<Game> joinableGames(){
        List<Game> joinableGames = new ArrayList<>();
        List<Game> allGames=gameRepository.findAll();
        for (Game game: allGames){
            if(game.getNumOfPlayersJoined()<4) {
                joinableGames.add(game);
            }
        }
        return joinableGames;
    }
    public void isInGame(String token,Long gameId){
        Game currentGame=gameRepository.findByGameId(gameId);
        User userByToken=userRepository.findByToken(token);
        if(!currentGame.getPlayerIds().contains(userByToken.getUserId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "player is not in this game!");
        }
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
        if (playerRepository.findByPlayerId(userRepository.findByToken(token).getUserId())!=null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Player already joined another game.");
        }
        Game game = new Game();
        game.setGameName(gameInput.getGameName());
        game.setCardCzarMode(gameInput.isCardCzarMode());
        game.setNumOfPlayersJoined(1);
        game.setCurrentGameRoundIndex(0);
        game.setNumOfRounds(gameInput.getNumOfRounds());
        game.setGameEdition(gameInput.getGameEdition());
        // admin player is the one who creates game
        Player adminPlayer = createPlayer(token);
        addPlayerToGame(adminPlayer,game);

        Deck d=createDeck(game.getGameEdition());
        game.setDeckID(d.getDeckId());
        game = gameRepository.saveAndFlush(game);

        return game;
    }

    // NOT NEEDED AS IT IS HANDLED IN THE leaveWaitingArea
//    // GAME DELETION - when last player leaves waiting area
//    public void deleteGameInWaitingArea(Long gameId, String token) {
//        // finding game
//        Game game = gameRepository.findByGameId(gameId);
//        if (game == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with given Id doesn't exist!");
//        }
//        // check whether game has only one player, and it is the player that made the request
//        if (game.getNumOfPlayersJoined() > 1) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game has more than one player!");
//        }
//        User user = userRepository.findByToken(token); // ! PLAYER ID SAME AS USER ID !
//        List<Long> playerIds = game.getPlayerIds();
//        if (!playerIds.contains(user.getUserId())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not part of this game!");
//        }else{
//            // delete game
//            gameRepository.delete(game);
//            gameRepository.flush();
//        }
//    }


        //TODO throw an error, if Player/User is already in a game, or if the token is expired/user logged out --> ask Szymon
    private void addPlayerToGame(Player playerToAdd, Game game){
        if (game.getNumOfPlayersJoined() < 4){
            List<Long> players = game.getPlayerIds();
            players.add(playerToAdd.getPlayerId());
            game.setPlayerIds(players);
            List<String> currentPlayerNames=game.getPlayerNames();
            currentPlayerNames.add(playerToAdd.getPlayerName());
            game.setPlayerNames(currentPlayerNames);
            game.setNumOfPlayersJoined(game.getPlayerIds().size());}

        else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game already full! Join another game."); }
    }



    private void removePlayerFromGame(Game gameToLeave, User userToRemove) {
        // TODO delete player object for corresponding user?
        // TODO delete player completely
        for (Long playerId : gameToLeave.getPlayerIds()) {
            if (playerId.equals(userToRemove.getUserId())) {
                gameToLeave.getPlayerIds().remove(playerId);
                List<String> currentPlayerNames=gameToLeave.getPlayerNames();
                Player playerToDelete=playerRepository.findByPlayerId(playerId);
                currentPlayerNames.remove(playerToDelete.getPlayerName());
                gameToLeave.setPlayerNames(currentPlayerNames);
                playerRepository.delete(playerToDelete);
                playerRepository.flush();
                gameToLeave.decreasePlayers();
                return;
            }
        }
    }

    // function for joining a game
    public Game joinGame(Long gameId, String token){
        Game game = this.getGame(gameId);
        User userToJoin = userRepository.findByToken(token);
        if (userToJoin.getStatus() == UserStatus.ONLINE){
            if (!game.getPlayerIds().contains(userToJoin.getUserId())) {
                Player player = createPlayer(token);
                addPlayerToGame(player, game);
                gameRepository.saveAndFlush(game);
                // if the lobby is full, start the game
                if (game.getNumOfPlayersJoined() == 4)
                    this.startGame(game);
                return game;
            } else { throw new ResponseStatusException(HttpStatus.NO_CONTENT, "The user is already in the game!"); }
        } else { throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not logged in, cannot join a game!"); }
    }

    // helper method to actually start the game
    /* TODO: create a new GameRound (add it to the list of GameRounds and set
        it as the currentRound) and add a black card to this GameRound.
        Furthermore, create a method to give every player a role as well as
        10 white cards.
     */
   private void startGame(Game game) {
       // get not played white cards
        List <Card> whiteCards=cardRepository.findByDeckIdAndIsWhiteAndIsPlayed(game.getDeckID(),true,false);
        int upperbound=whiteCards.size();
       int[] arr = new int[upperbound];
       Random rand = new SecureRandom();
       Set<Integer> unique = new HashSet<Integer>();
       for (int i=0; i<40; i++)
       {
           int number = rand.nextInt(upperbound);
           while (unique.contains(number)){
               // `number` is a duplicate
               number = rand.nextInt(upperbound);
           }
           arr[i] = number;
           unique.add(number); // adding to the set
       }
       int card_Index=0;
       for(Long playerId: game.getPlayerIds()){
           Player currentPlayer=playerRepository.findByPlayerId(playerId);
           for(int i=0; i<10; i++){
               Card currentCard=whiteCards.get(arr[card_Index]);
               List <Card>playerCards=currentPlayer.getCardsOnHands();
               playerCards.add(currentCard);
               currentPlayer.setCardsOnHands(playerCards);
               currentCard.setPlayed(true);
               cardRepository.saveAndFlush(currentCard);
               card_Index++;
           }
           playerRepository.saveAndFlush(currentPlayer);
       }
        GameRound currentGameRound=gameRoundService.startNewRound(game);
        return;
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

    // method if a player decides to leave the game
    public void leaveGame(Long gameId, String token) {
        Game gameToLeave = this.getGame(gameId);
        User userToRemove = userRepository.findByToken(token);
        // if the user is not in the game, don't do anything (no error required)
        if (!gameToLeave.getPlayerIds().contains(userToRemove.getUserId()))
            return; // if it does not contain then return

        boolean lastPlayer = gameToLeave.getNumOfPlayersJoined() == 1;
        // remove the player
        removePlayerFromGame(gameToLeave, userToRemove);
        // if it is the last player, also delete the game
        if (lastPlayer) {
            gameRepository.delete(gameToLeave);
            gameRepository.flush();
        }
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
        player.setNumberOfPicked(0);
        player = playerRepository.saveAndFlush(player);
        return player;
    }


    public Deck createDeck(String gameEdition) {

        Deck d = new Deck();
        d.setDeckName(gameEdition);

        List<Card> cards = new ArrayList<>();

        String pathBlack;
        String pathWhite;

        List<String> blackCards = new ArrayList<>(); //test printing out all the Cards on Console
        List<String> whiteCards = new ArrayList<>(); //test printing out all the Cards on Console

        if (gameEdition.equals("regular")) {
            pathBlack = "src/main/resources/CAH Base Set Black.csv";
            pathWhite = "src/main/resources/CAH Base Set White.csv";
        }
        else {
            pathBlack = "src/main/resources/CAH Base Set White.csv";
            pathWhite = "src/main/resources/CAH Base Set White.csv";
        }
        try (BufferedReader br = new BufferedReader(new FileReader(pathBlack))) {
            String line;
            while ((line = br.readLine()) != null) {
                Card c = new Card();
                c.setCardText(line);
                c.setWhite(false);
                c.setGameEdition(gameEdition); // Ege needed this for developing the Repo --> can be deleted
                c.setPlayed(false);
                cards.add(c);
                //cardRepository.save(c);
                blackCards.add(line); //test printing out all the Cards on Console
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(pathWhite))) {
            String line;
            while ((line = br.readLine()) != null) {
                Card c = new Card();
                c.setCardText(line);
                c.setWhite(true);
                c.setGameEdition(gameEdition); // Ege needed this for developing the Repo --> can be deleted
                c.setPlayed(false);
                cards.add(c);
                //cardRepository.save(c);
                whiteCards.add(line);//test printing out all the Cards on Console
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        //test printing out all the Cards on Console
        cards=cardRepository.saveAll(cards);

        cardRepository.flush();
        //d.setCards(cards);
        d=deckRepository.save(d);
        //List<Card> test = d.getCards();
        for(Card card: cards ){
            //System.out.println(card.getCardText());
            card.setDeckId(d.getDeckId());
            //cardRepository.save(card);
        }

        //TODO delete deck/cards after game is finished
        cardRepository.saveAll(cards);
        cardRepository.flush();
        deckRepository.flush();
        //System.out.println("EntityDeck"); //test printing out all the Cards on Console
        //System.out.println(d.getCards()); //test printing out all the Cards on Console
        //System.out.println("Cards:"); //test printing out all the Cards on Console
        //System.out.println(whiteCards); //test printing out all the Cards on Console
        //System.out.println(blackCards); //test printing out all the Cards on Console
        return d;
    }

    public Player getPlayer(String token){
       User userByToken=userRepository.findByToken(token);
       Player playerToGet=playerRepository.findByPlayerId(userByToken.getUserId());
       return playerToGet;
    }

    public GameRound getGameRound(Long gameId){
       Game gameById=gameRepository.findByGameId(gameId);
       GameRound gameRoundToGet=gameRoundRepository.findByRoundId(gameById.getCurrentGameRoundId());
       return gameRoundToGet;

    }




}
