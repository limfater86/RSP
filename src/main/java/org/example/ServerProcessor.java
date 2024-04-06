package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.example.core.IBattleService;
import org.example.net.IDecoderProcessor;

import java.util.HashMap;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public class ServerProcessor implements IDecoderProcessor {
    private Channel channel = null;
    private HashMap<String, IRunnable> methodsMap;
    private String playerName;
    private IBattleService battleService;

    public ServerProcessor(IBattleService battleService) {
        this.battleService = battleService;
        initMethodsMap();
    }

    private void initMethodsMap() {
        methodsMap = new HashMap<>();
        methodsMap.put("start", this::startGame);
        methodsMap.put("stop", this::stopGame);
        methodsMap.put("rock", this::stopGame);
        methodsMap.put("scissor", this::stopGame);
        methodsMap.put("paper", this::stopGame);
    }

    private void startGame() {

    }

    private void stopGame() {

    }

    private void rock() {

    }

    private void scissors() {

    }

    private void paper() {

    }

    @Override
    public void process(String message, Channel channel) {
        this.channel = channel;
        if (playerName == null || playerName.isEmpty()) {
            playerName = message;
            if (battleService.registerNewPlayer(playerName, this)) {
                sendResponse("To Start the Game send 'start' command");
            } else {
                sendResponse("This Name is already used. Please change the Name");
            }
        } else {
            execute(message);
        }
    }

    private void execute(String command) {
        if (command == null || command.isEmpty()) return;

        IRunnable runnable = methodsMap.get(command);
        if (runnable != null) {
            runnable.run();
        } else {
            sendResponse("Error. Wrong command.");
        }
    }

    private void sendResponse(String message) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(message.getBytes());
        channel.write(buffer);
        channel.flush();
    }


    @Override
    public void release() {

    }

    @Override
    public void registerChannel(Channel channel) {
        this.channel = channel;
    }
}
