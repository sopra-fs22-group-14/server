package ch.uzh.ifi.hase.soprafs22.rest.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GamePostDTO {
    private String gameEdition;
    private boolean cardCzarMode;
    private String gameName;
    private int numOfRounds;
}
