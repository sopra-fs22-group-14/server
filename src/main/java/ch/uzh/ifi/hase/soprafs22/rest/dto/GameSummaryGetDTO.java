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


public class GameSummaryGetDTO {
    private List<String> playersNames;//change of an "s" player(s)Names for Szymon/Frontend
    private List<Integer> playersNumbersOfPicked;
    private List<String> winnersNames;
    private List<Long> winnersIds;
}
