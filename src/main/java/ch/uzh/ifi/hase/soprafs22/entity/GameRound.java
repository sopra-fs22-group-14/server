package ch.uzh.ifi.hase.soprafs22.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GAMEROUND")
public class GameRound {

    @Id
    @GeneratedValue
    private Long roundId;

    @Column(nullable = false)
    private int roundNumber;

    //@OneToOne //correct to solve "'Basic' attribute type should not be 'Persistence Entity'"?
    //@Column(nullable = false)
    //private Card blackCard;

    //@Column(nullable = false)
    //@ElementCollection
    //private List<Long> playedCardsId = new ArrayList<>();

    //@Column(nullable = false, unique=true)
    //@ElementCollection
    //private List<Player> accordingPlayer = new ArrayList<>;

    //@Column(nullable = false)
    //@ElementCollection
    //private List<int> pointsPerPlayer = new ArrayList<>;
}
