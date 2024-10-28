package tech.yizhichan.sdk.listener;

import tech.yizhichan.common.model.sse.SseMsgIdEnum;

/**
 * @description: SseDataStreamListener
 * @author: lex
 * @date: 2024-09-01
 **/
public interface SseDataStreamListener {

    SseMsgIdEnum messageId();

    /**
     * onMessage
     *
     * @param message message
     */
    void onMessage(String message);
}
