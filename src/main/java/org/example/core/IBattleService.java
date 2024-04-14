package org.example.core;

import org.example.ServerProcessor;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public interface IBattleService {
    boolean registerNewPlayer(String playerName, ServerProcessor playerProcessor);
    void removePlayer(String playerName);

    void startBattle(String playerName);

    void stopBattle(BattleRoom room);

    void endBattle(BattleRoom room);
}
