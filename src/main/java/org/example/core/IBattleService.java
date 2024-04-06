package org.example.core;

import org.example.ServerProcessor;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public interface IBattleService {
    boolean registerNewPlayer(String playerName, ServerProcessor playerProcessor);

    void startBattle(String playerName);

    void stopBattle(String playerName, long battleId);

    void hitRock(String playerName, long battleId);

    void hitScissors(String playerName, long battleId);

    void hitPaper(String playerName, long battleId);
}