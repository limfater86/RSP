package org.example.net;

import io.netty.channel.Channel;

/**
 * @author dperminov
 * @since 06.04.2024
 */
public interface IDecoderProcessor {

    /**
     * Обработать входящий пакет
     *
     * @param message - данные
     * @param channel - канал соединения
     */
    void process(String message, Channel channel);

    /**
     * Освободить данные инициализированные в декодере, вызывается при закрытии соединения
     */
    void release();

    /**
     * Установить канал для отправки сообщений при установке соединения
     *
     * @param channel
     */
    void registerChannel(Channel channel);
}
