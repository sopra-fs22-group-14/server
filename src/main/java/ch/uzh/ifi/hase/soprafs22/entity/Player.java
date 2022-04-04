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
    @GeneratedValue
    private Long playerId;

    @Column(nullable = false, unique = true)
    private String playerName;

    @Column(nullable = false)
    private boolean isCardCzar;

    @Column(nullable = false)
    private boolean isPlaying;

    @Column(nullable = false)
    private int RoundsWon;

    //TODO just store Id's or the object's
    //@Column(nullable = false)
    //@ElementCollection
    //private List<Cards> cardsOnHands = new ArrayList<Cards>;

    //@Column(nullable = false)
    //@ElementCollection
    //private List<String> CombinationsOfGame=new ArrayList<String>();




}
