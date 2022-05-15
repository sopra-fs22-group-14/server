package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.GameRound;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);
    // --- build gradle gives warning unmapped properties |!|

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    UserGetDTO convertEntityToUserGetDTO(User user);
    // --- build gradle gives warning unmapped properties |!|




    // Need when user logs in - while registering + while logging in
    /** PRODUCTION READY*/
    /** SZYMON */
    @Mapping(source = "userId", target = "id")
    @Mapping(source = "token", target = "token")
    UserLoginDTO convertEntityToUserLoginDTO(User user);


    // FOR GETTING THE GAME - LOBBY (and latest round winner)
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "cardCzarMode", target = "cardCzarMode")
    @Mapping(source = "gameEdition", target = "gameEdition")
    @Mapping(source = "numOfPlayersJoined", target = "numOfPlayersJoined")
    @Mapping(source = "numOfRounds", target = "numOfRounds")
    @Mapping(source = "latestRoundWinner", target = "latestRoundWinner")
    @Mapping(source = "latestWinningCardText", target = "latestWinningCardText")
    @Mapping(source = "playerNames", target = "playerNames")
    @Mapping(source = "currentGameRoundIndex", target = "currentGameRoundIndex")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "cardCzarMode", target = "cardCzarMode")
    @Mapping(source = "gameEdition", target = "gameEdition")
    @Mapping(source = "numOfRounds", target = "numOfRounds")
    Game convertGamePostDTOToEntity(GamePostDTO gamePostDTO);

    @Mapping(source = "gameId", target = "gameId")
    Game convertGamePutDTOToEntity(GamePutDTO gamePutDTO);

    @Mapping(source = "playerId", target = "playerId")
    @Mapping(source = "playerName", target = "playerName")
    @Mapping(source = "cardCzar", target = "cardCzar")
    @Mapping(source = "playing", target = "playing")
    @Mapping(source = "roundsWon", target = "roundsWon")
    @Mapping(source = "cardsOnHands", target = "cardsOnHands")
    @Mapping(source = "playedCombinations", target = "playedCombinations")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player);

    @Mapping(source = "roundId", target = "roundId")
    @Mapping(source = "blackCard", target = "blackCard")
    @Mapping(source = "cardCzarId", target = "cardCzarId")
    @Mapping(source = "playedCards", target = "playedCards")
    @Mapping(source = "final", target = "final")
    GameRoundGetDTO convertEntityToGameRoundGetDTO(GameRound gameRound);

    @Mapping(source = "playerNames", target = "playersNames") //change of an "s" player(s)Names for Szymon/Frontend
    @Mapping(source = "playersNumbersOfPicked", target = "playersNumbersOfPicked")
    @Mapping(source = "winnersNames", target = "winnersNames")
    @Mapping(source = "winnersIds", target = "winnersIds")
    GameSummaryGetDTO convertEntityToGameSummaryGetDTO(Game game);

    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "username", target = "username")
    UserProfileGetDTO convertEntityToUserProfileGetDTO(User requestedUser);


}
