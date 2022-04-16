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
@Table(name = "DECK")
public class Deck {

    @Id
    @GeneratedValue
    private Long deckId;

    @Column()
    private String deckName;

    //TODO just store Id's or the object's
    @Column()
    //@ElementCollection
    @OneToMany
    private List<Card> cards = new ArrayList<>();


}
