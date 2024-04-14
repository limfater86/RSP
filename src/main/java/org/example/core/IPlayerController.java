package org.example.core;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public interface IPlayerController {
    void sendMessage(String message);

    void setBattleRoom(BattleRoom room);

    String getPlayerName();
}
