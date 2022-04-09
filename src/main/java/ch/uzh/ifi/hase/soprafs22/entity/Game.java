package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(nullable = false, unique = true)
    private String gameName;

    @Column(nullable = false)
    private boolean cardCzarMode;

    @Column(nullable = false)
    private String gameEdition;

    @Column
    private int numOfPlayersJoined;

    @Column
    private int numOfRounds;

    //TODO we might need a gamestatus

    //TODO just store Id's or the object's
    //@Column(nullable = false, unique=true)
    //@ElementCollection
    //private List<Player> playerList = new ArrayList<>;
    @Column()
    @ElementCollection
    private List<Long> playerIds = new ArrayList<>();

    //TODO this also works decide which one to use
    //@OneToMany
    //private List<Player> playerList = new ArrayList<>();

    //@Column(nullable = false)
    //@ElementCollection
    //private List<GameRound> rounds = new ArrayList<>;

}
