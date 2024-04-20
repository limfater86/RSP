package org.example.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.example.net.IDecoderProcessor;

import java.util.HashMap;

import static org.example.Messages.*;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class ServerProcessor implements IDecoderProcessor, IPlayerController {
    private Channel channel = null;
    private HashMap<String, Runnable> methodsMap;
    private String playerName;
    private IBattleService battleService;
    private BattleRoom battleRoom;

    public ServerProcessor(IBattleService battleService) {
        this.battleService = battleService;
        initMethodsMap();
    }

    private void initMethodsMap() {
        methodsMap = new HashMap<>();
        methodsMap.put("start", this::startGame);
        methodsMap.put("stop", this::stopGame);
        methodsMap.put("exit", this::exit);
        methodsMap.put("rock", this::rock);
        methodsMap.put("scissors", this::scissors);
        methodsMap.put("paper", this::paper);
    }

    private void startGame() {
        if (playerName == null || playerName.isEmpty()) {
            sendResponse(ON_CONNECT_MESSAGE);
            return;
        }
        battleService.startBattle(playerName);
    }

    private void stopGame() {
        if (checkGame()) {
            battleService.stopBattle(battleRoom);
        }
    }

    private void exit() {
        release();
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }

    private void rock() {
        hit(HitType.Rock);
    }

    private void scissors() {
        hit(HitType.Scissors);
    }

    private void paper() {
        hit(HitType.Paper);
    }

    private void hit(HitType hitType) {
        if (checkGame()) {
            battleRoom.hit(playerName, hitType);
        }
    }

    private boolean checkGame() {
        if (battleRoom == null) {
            sendResponse(GAME_NOT_STARTED_ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void setPlayerName(String name) {
        playerName = name;
    }

    public void setBattleRoom(BattleRoom battleRoom) {
        this.battleRoom = battleRoom;
    }

    private void execute(Channel channel, String command) {
        if (command == null || command.isEmpty()) return;

        Runnable runnable = methodsMap.get(command);
        if (runnable != null) {
            runnable.run();
        } else {
            sendResponse(channel, WRONG_COMMAND_ERROR_MESSAGE);
        }
    }

    private void sendResponse(String message) {
        sendResponse(this.channel, message);
    }

    private void sendResponse(Channel channel, String message) {
        if (channel == null) return;

        if (channel.isActive()) {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(message.getBytes());
            channel.write(buffer);
            channel.flush();
        }
    }

    @Override
    public void process(String message, Channel channel) {
        if (playerName == null || playerName.isEmpty()) {
            battleService.registerNewPlayer(message, this);
        } else {
            execute(channel, message);
        }
    }

    @Override
    public void sendMessage(String message) {
        sendResponse(message);
    }

    @Override
    public void release() {
        if (battleRoom != null) {
            battleService.stopBattle(battleRoom);
            battleRoom = null;
        }
        battleService.removePlayer(playerName);
    }

    @Override
    public void registerChannel(Channel channel) {
        this.channel = channel;
    }
}
