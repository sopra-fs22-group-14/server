package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGetDTO {
    private long playerId;
    private String playerName;
    private boolean isCardCzar;
    private boolean isPlaying;
    private int roundsWon;
    private List<Card> cardsOnHands;
    private List<String> playedCombinations;
}
