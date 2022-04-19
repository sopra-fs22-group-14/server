package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.CardColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CARD")
public class Card implements Serializable {

    @Id
    @GeneratedValue
    private Long cardId;

    @Column()
    private boolean isWhite;

    @Column()
    private String cardText;

    //@JoinColumn(name="cardId") //add FK
    @Column
    private String gameEdition;
    @Column
    private Long deckId;
    @Column
    private boolean isPlayed;
}
