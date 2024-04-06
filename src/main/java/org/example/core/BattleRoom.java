package org.example.core;

import org.example.ServerProcessor;

import java.util.HashMap;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class BattleRoom {
    private HashMap<String, IPlayerController> playerControllers;
    private HashMap<String, HitType> playersHits;
    private boolean isBattleEnd = false;
    private long roomId;

    public BattleRoom(long roomId) {
        this.roomId = roomId;
        playerControllers = new HashMap<>();
        playersHits = new HashMap<>();
    }

    public void addPlayer(String playerName, IPlayerController controller) {
        playerControllers.put(playerName, controller);
    }

    public void hit(String playerName, HitType hit) {
        if (playersHits.containsKey(playerName)) {
            playerControllers.get(playerName).sendMessage("You Already hit. You can't change hit or hit twice");
            return;
        }
        playersHits.put(playerName, hit);
        if (playersHits.size() > 1) checkWinner();
    }

    private void checkWinner() {

    }

    public boolean isBattleEnd() {
        return isBattleEnd;
    }

    public void reset() {
        roomId = -1;
        playerControllers.clear();
        playersHits.clear();
    }
}
