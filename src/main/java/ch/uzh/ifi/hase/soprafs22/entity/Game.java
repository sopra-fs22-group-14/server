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

    @Column(nullable = false, unique = true)
    private String gameName;

    @Column(nullable = false)
    private boolean cardCzarMode;

    @Column(nullable = false)
    private String gameEdition;

    @Column
    private int numOfPlayersJoined;







}
