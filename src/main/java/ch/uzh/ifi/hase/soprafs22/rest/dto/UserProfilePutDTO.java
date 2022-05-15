package ch.uzh.ifi.hase.soprafs22.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePutDTO {

  private String username;
  private Date birthday;
  private String password;


}
