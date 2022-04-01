package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.GameRound;
import ch.uzh.ifi.hase.soprafs22.repository.GameRoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GameRoundService {

    private Logger log = LoggerFactory.getLogger(GameRoundService.class);

    private GameRoundRepository gameRoundRepository;

    public GameRoundService(@Qualifier("gameRoundRepository")GameRoundRepository gameRoundRepository) {
        this.gameRoundRepository = gameRoundRepository;
    }
    public List<GameRound> getAllGameRounds(){
        return this.gameRoundRepository.findAll();
    }
}
