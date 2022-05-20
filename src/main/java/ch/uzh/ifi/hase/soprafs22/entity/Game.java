package ch.uzh.ifi.hase.soprafs22.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GAME")

public class Game implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;

    @Column(unique = true)
    private String gameName;

    @Column
    private boolean cardCzarMode;

    @Column
    private String gameEdition;

    @Column
    private boolean isActive;

    @Column
    private int numOfPlayersJoined;

    @Column
    private int numOfRounds;

    @Column
    // this value will be used to display the winner in the front end
    private String latestRoundWinner;

    @Column
    // this value will be used to display the winner in the front end
    private String latestWinningCardText;

    @Column
    //this will start from zero when game is initialized
    // during the first round it will be 1 then for the second round two etc
    // this way we can keep track of number of rounds and so that we can change card Czar by modulo 4
    private int currentGameRoundIndex;

    @Column
    private Long currentGameRoundId;

    @Column
    private Long deckID;

    //@Column(nullable = false, unique=true)
    //@ElementCollection
    //private List<Player> playerList = new ArrayList<>;

    @Column
    @ElementCollection
    private List<Long> playerIds = new ArrayList<>();

    @Column
    @ElementCollection
    private List<Long> roundIds = new ArrayList<>();

    @Column
    @ElementCollection
    private List<String> playerNames=new ArrayList<>();

    @Column
    @ElementCollection
    private List<Integer> playersNumbersOfPicked=new ArrayList<>();

    @Column
    @ElementCollection
    private List<String> winnersNames=new ArrayList<>();

    @Column
    @ElementCollection
    private List<Long> winnersIds=new ArrayList<>();


    //@Column(nullable = false)
    //@ElementCollection
    //private List<GameRound> rounds = new ArrayList<>;

    public void decreasePlayers() {
        this.numOfPlayersJoined--;
    }

}
