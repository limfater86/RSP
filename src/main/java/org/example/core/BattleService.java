package org.example.core;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.example.syncable.Syncable;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.example.ExceptionMessage.*;
import static org.example.Messages.*;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class BattleService implements IBattleService {
    private final HashMap<String, IPlayerController> players;
    private final LinkedHashSet<String> playersReadyToBattle;
    private final ObjectPool<BattleRoom> battleRooms;
    private final ReentrantReadWriteLock reentrantLock = new ReentrantReadWriteLock();

    public BattleService() {
        players = new HashMap<>();
        playersReadyToBattle = new LinkedHashSet<>();
        battleRooms = new GenericObjectPool<>(new BattleRoomFactory());
    }

    @Override
    public void registerNewPlayer(String playerName, ServerProcessor playerProcessor) {
        Syncable.S.syncWriteAndRun(reentrantLock, () -> {
            if (players.containsKey(playerName)) {
                playerProcessor.sendMessage(PLAYER_NAME_ERROR_MESSAGE);
                return;
            }

            players.put(playerName, playerProcessor);
            playerProcessor.setPlayerName(playerName);
            playerProcessor.sendMessage(ON_REGISTER_PLAYER_MESSAGE);
        });
    }

    @Override
    public void removePlayer(String playerName) {
        Syncable.S.syncWriteAndRun(reentrantLock, () -> {
            players.remove(playerName);
            playersReadyToBattle.remove(playerName);
        });
    }

    @Override
    public void startBattle(String playerName) {
        Syncable.S.syncWriteAndRun(reentrantLock, () -> internalStartBattle(playerName));
    }

    private void internalStartBattle(String playerName) {
        if (playersReadyToBattle.isEmpty()) {
            playersReadyToBattle.add(playerName);
            players.get(playerName).sendMessage(WAITING_OPPONENT_MESSAGE);
            return;
        }
        if (playersReadyToBattle.contains(playerName)) {
            players.get(playerName).sendMessage(WAITING_OPPONENT_ERROR_MESSAGE);
            return;
        }

        IPlayerController player = players.get(playerName);
        Iterator<String> iterator = playersReadyToBattle.iterator();
        String opponentName = iterator.next();
        iterator.remove();
        IPlayerController opponent = players.get(opponentName);

        if (opponent == null) throw new RuntimeException(OPPONENT_ERROR_MESSAGE);

        BattleRoom room = null;
        try {
            room = battleRooms.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException(POOL_ERROR_MESSAGE + e);
        }

        room.addPlayer(playerName, player);
        room.addPlayer(opponentName, opponent);
        room.setBattleService(this);
        opponent.setBattleRoom(room);
        player.setBattleRoom(room);
        room.startBattle();
    }

    @Override
    public void stopBattle(BattleRoom room) {
        room.stopBattle();
        endBattle(room);
    }

    @Override
    public void endBattle(BattleRoom room) {
        room.reset();
        try {
            battleRooms.returnObject(room);
        } catch (Exception e) {
            //ignored
        }
    }
}
