package ch.uzh.ifi.hase.soprafs22.rest.dto;
import ch.uzh.ifi.hase.soprafs22.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRoundGetDTO {
    private Long roundId;
    private Card blackCard;
    private long cardCzarId;
}
