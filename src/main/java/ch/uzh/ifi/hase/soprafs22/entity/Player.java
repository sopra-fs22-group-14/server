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
@Table(name = "PLAYER")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long playerId;

    @Column(unique = true)
    private String playerName;

    @Column
    private boolean isCardCzar;

    @Column
    private boolean isPlaying;

    @Column
    private int roundsWon;

    @Column
    private int numberOfPicked;

    @Column
    @OneToMany
    private List<Card> cardsOnHands=new ArrayList<>();

    @Column
    private boolean hasPicked;

    @Column
    @ElementCollection
    private List<String> playedCombinations=new ArrayList<>();

    @Column
    private Long currentGameId = 0L;

    //@Column(nullable = false)
    //@ElementCollection
    //private List<String> CombinationsOfGame=new ArrayList<String>();

}
