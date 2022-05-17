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
  private int totalRoundWon;
  private int timesPicked;
  private int totalGameWon;
  private List<String> bestCombinations;

}
