package ch.uzh.ifi.hase.soprafs22.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
