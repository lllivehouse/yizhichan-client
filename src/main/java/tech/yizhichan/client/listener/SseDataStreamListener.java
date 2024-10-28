package tech.yizhichan.client.listener;

import tech.zhizheng.common.model.sse.SseMsgIdEnum;

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
