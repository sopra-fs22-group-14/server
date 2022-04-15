package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    //TODO not sure about jsonignore properties
    @OneToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn
    private Card blackCard;

    // we might store the cardCzarId
    //@Column
    //private int cardCzarId;


    //@Column(nullable = false)
    //@ElementCollection
    //private List<Long> playedCardsId = new ArrayList<>();

    //@Column(nullable = false, unique=true)
    //@ElementCollection
    // this list will  keep track of who played which card
    //private List<Player> accordingPlayer = new ArrayList<>;

    //@Column(nullable = false)
    //@ElementCollection
    //private List<int> pointsPerPlayer = new ArrayList<>;
}
