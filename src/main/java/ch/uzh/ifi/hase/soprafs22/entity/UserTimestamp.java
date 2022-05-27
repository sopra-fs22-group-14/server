package ch.uzh.ifi.hase.soprafs22.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERTIMESTAMP")
public class UserTimestamp implements Serializable {

    @Id
    private Long userId;

    @Column(nullable = false)
    private Date lastGameRequest = new Date();

    @Column(nullable = false)
    private Date lastSeen = new Date();

}
