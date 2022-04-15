package ch.uzh.ifi.hase.soprafs22.entity;

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

    @Column(nullable = false)
    private boolean isWhite;

    @Column(nullable = false)
    private String cardText;

}
