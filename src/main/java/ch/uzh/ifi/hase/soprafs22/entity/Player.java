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

    //TODO just store Id's or the object's
    @Column
    @OneToMany
    private List<Card> cardsOnHands=new ArrayList<>();

    //@Column(nullable = false)
    //@ElementCollection
    //private List<String> CombinationsOfGame=new ArrayList<String>();




}
