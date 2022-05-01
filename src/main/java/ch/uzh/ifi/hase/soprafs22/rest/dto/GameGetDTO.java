package ch.uzh.ifi.hase.soprafs22.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor



public class GameGetDTO {
    private Long gameId;
    private String gameName;
    private boolean cardCzarMode;
    private String gameEdition;
    private int numOfPlayersJoined;
    private int numOfRounds;
    private String latestRoundWinner;
    private String latestWinningCardText;
    private List<String> playerNames;
    private int currentGameRoundIndex;
}
