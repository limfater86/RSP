package org.example.core;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.example.ServerProcessor;

import java.util.*;

import static org.example.Messages.*;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class BattleService implements IBattleService {
    private final HashMap<String, IPlayerController> players;
    private final LinkedHashSet<String> playersReadyToBattle;
    private final ObjectPool<BattleRoom> battleRooms;

    public BattleService() {
        players = new HashMap<>();
        playersReadyToBattle = new LinkedHashSet<>();
        battleRooms = new GenericObjectPool<>(new BattleRoomFactory());
    }

    @Override
    public boolean registerNewPlayer(String playerName, ServerProcessor playerProcessor) {
        if (players.containsKey(playerName)) return false;

        players.put(playerName, playerProcessor);

        return true;
    }

    @Override
    public void removePlayer(String playerName) {
        players.remove(playerName);
        playersReadyToBattle.remove(playerName);
    }

    @Override
    public void startBattle(String playerName) {
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

        if (opponent == null) throw new RuntimeException("Opponent is null");

        BattleRoom room = null;
        try {
            room = battleRooms.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("Unable to borrow buffer from pool" + e);
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
