package org.example.core;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public interface IBattleService {
    void registerNewPlayer(String playerName, ServerProcessor playerProcessor);

    void removePlayer(String playerName);

    void startBattle(String playerName);

    void stopBattle(BattleRoom room);

    void endBattle(BattleRoom room);
}
