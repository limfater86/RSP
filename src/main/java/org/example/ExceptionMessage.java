package org.example;

/**
 * @author dperminov
 * @since 20.04.2024
 */
public class ExceptionMessage {
    public static final String POOL_ERROR_MESSAGE = "Unable to borrow buffer from pool";
    public static final String OPPONENT_ERROR_MESSAGE = "Opponent is null";
    public static final String DECODER_READ_CTX_ERROR_MESSAGE = "Decoder: channelRead: argument ctx was null";
    public static final String DECODER_READ_MSG_ERROR_MESSAGE = "Decoder: channelRead: msg ctx was null";
    public static final String DECODER_REGISTER_CTX_ERROR_MESSAGE = "Decoder: channelReadComplete: argument ctx was null";
    public static final String DECODER_READ_COMPLETE_CTX_ERROR_MESSAGE = "Decoder: channelRegistered: argument ctx was null";
}
