package ch.uzh.ifi.hase.soprafs22.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRecordsGetDTO {

  private String username;
  private int totalGamePlayed;
  private int totalGameWon;
  private int totalRoundPlayed;
  private int totalRoundWon;
  private List<String> bestCombinations;

}
