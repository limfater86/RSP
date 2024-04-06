package org.example.core;

import org.example.ServerProcessor;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class BattleService implements IBattleService {
    private HashMap<String, ServerProcessor> players;
    private HashSet<String> playersReadyToBattle;
    private HashMap<Long, BattleRoom> battleRooms;

    public BattleService() {
        players = new HashMap<>();
        playersReadyToBattle = new HashSet<>();
        battleRooms = new HashMap<>();
    }

    @Override
    public boolean registerNewPlayer(String playerName, ServerProcessor playerProcessor) {
        if (players.containsKey(playerName)) return false;

        players.put(playerName, playerProcessor);

        return true;
    }

    @Override
    public void startBattle(String playerName) {

    }

    @Override
    public void stopBattle(String playerName, long battleId) {

    }

    @Override
    public void hitRock(String playerName, long battleId) {

    }

    @Override
    public void hitScissors(String playerName, long battleId) {

    }

    @Override
    public void hitPaper(String playerName, long battleId) {

    }
}
