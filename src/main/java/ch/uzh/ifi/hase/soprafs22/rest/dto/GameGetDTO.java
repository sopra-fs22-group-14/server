package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class GameGetDTO {
    private Long id;
    private String name;
    private boolean gameMode;
    private String cardsType;
    private int numberOfPlayers;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGameMode() {
        return gameMode;
    }

    public void setGameMode(boolean gameMode) {
        this.gameMode = gameMode;
    }

    public String getCardsType() {
        return cardsType;
    }

    public void setCardsType(String cardsType) {
        this.cardsType = cardsType;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
}
