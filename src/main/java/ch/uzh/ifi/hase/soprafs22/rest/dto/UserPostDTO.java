package ch.uzh.ifi.hase.soprafs22.rest.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPostDTO {

  private String username;
  private String password;

}
