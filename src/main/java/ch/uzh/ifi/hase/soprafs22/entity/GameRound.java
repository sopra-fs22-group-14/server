package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Column
    private Long correspondingGameId;

    //@Column(nullable = false)
    //private int roundNumber;

    @OneToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn
    private Card blackCard;

    // we might store the cardCzarId
    @Column
    private Long cardCzarId;

    //id of player and id of played card
    @Column()
    @ElementCollection
    private Map<Long,Long> cardAndPlayerIds = new HashMap<>();

    @Column()
    @OneToMany
    private List<Card> playedCards = new ArrayList<>(); // for displaying played cards in the gameround

    @Column
    private String roundWinnerName;

    @Column
    private Long roundWinnerId;

    @Column
    private int numberOfPicked;

    @Column
    private boolean isFinal;


    //@Column(nullable = false, unique=true)
    //@ElementCollection
    // this list will  keep track of who played which card
    //private List<Player> accordingPlayer = new ArrayList<>;

    //@Column(nullable = false)
    //@ElementCollection
    //private List<int> pointsPerPlayer = new ArrayList<>;
}
