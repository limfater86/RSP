package org.example.core;


import org.example.syncable.Syncable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.example.Messages.*;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class BattleRoom {
    private final HashMap<String, IPlayerController> playerControllers;
    private final HashMap<String, HitType> playersHits;
    private IBattleService battleService;
    private final ReentrantReadWriteLock reentrantLock = new ReentrantReadWriteLock();

    public BattleRoom() {
        playerControllers = new HashMap<>();
        playersHits = new HashMap<>();
    }

    public void setBattleService(IBattleService battleService) {
        this.battleService = battleService;
    }

    public void addPlayer(String playerName, IPlayerController controller) {
        playerControllers.put(playerName, controller);
    }

    public void startBattle() {
        for (IPlayerController playerController : playerControllers.values()) {
            playerController.sendMessage(START_MESSAGE);
        }
    }

    public void hit(String playerName, HitType hit) {
        Syncable.S.syncWriteAndRun(reentrantLock, () -> internalHit(playerName, hit));

        if (playersHits.size() > 1) {
            Syncable.S.syncReadAndRun(reentrantLock, this::checkWinner);
            handleEndGame();
        }
    }

    private void internalHit(String playerName, HitType hit) {
        if (playersHits.containsKey(playerName)) {
            playerControllers.get(playerName).sendMessage(HIT_ERROR_MESSAGE);
            return;
        }
        playersHits.put(playerName, hit);
    }

    public void stopBattle() {
        for (IPlayerController playerController : playerControllers.values()) {
            playerController.setBattleRoom(null);
            playerController.sendMessage(STOP_BATTLE_MESSAGE);
        }
    }

    private void checkWinner() {
        Iterator<IPlayerController> iterator = playerControllers.values().iterator();
        IPlayerController firstPlayer = iterator.next();
        IPlayerController secondPlayer = iterator.next();

        HitType firstPlayerHit = playersHits.get(firstPlayer.getPlayerName());
        HitType secondPlayerHit = playersHits.get(secondPlayer.getPlayerName());

        if (firstPlayerHit == secondPlayerHit) {
            firstPlayer.sendMessage(DRAW_BATTLE_MESSAGE);
            secondPlayer.sendMessage(DRAW_BATTLE_MESSAGE);
            playersHits.clear();
            return;
        }

        if (firstPlayerHit.isBeat(secondPlayerHit)) {
            firstPlayer.sendMessage(WIN_BATTLE_MESSAGE);
            secondPlayer.sendMessage(LOSE_BATTLE_MESSAGE);
        } else {
            firstPlayer.sendMessage(LOSE_BATTLE_MESSAGE);
            secondPlayer.sendMessage(WIN_BATTLE_MESSAGE);
        }
    }

    private void handleEndGame() {
        battleService.endBattle(this);
    }

    public void reset() {
        playerControllers.clear();
        playersHits.clear();
        battleService = null;
    }
}
