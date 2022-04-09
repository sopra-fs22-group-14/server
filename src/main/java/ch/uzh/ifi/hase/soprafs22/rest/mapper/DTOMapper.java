package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
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


    // FOR GETTING THE GAME - LOBBY
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "cardCzarMode", target = "cardCzarMode")
    @Mapping(source = "gameEdition", target = "gameEdition")
    @Mapping(source = "numOfPlayersJoined", target = "numOfPlayersJoined")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "cardCzarMode", target = "cardCzarMode")
    @Mapping(source = "gameEdition", target = "gameEdition")
    Game convertGamePostDTOToEntity(GamePostDTO gamePostDTO);
}
