package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import org.springframework.beans.factory.annotation.Qualifier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class GameService {
    private Logger log = LoggerFactory.getLogger(GameService.class);
    private GameRepository gameRepository;

    public GameService(@Qualifier("gameRepository")GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    public List<Game> getAllGames(){
        return this.gameRepository.findAll();
    }

}
