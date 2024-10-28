package tech.yizhichan.sdk.listener;

import tech.yizhichan.common.model.sse.SseMsgIdEnum;
import tech.yizhichan.common.utils.GsonFactory;
import tech.yizhichan.sdk.queue.HotfixProcessParameter;
import tech.yizhichan.sdk.queue.HotfixProcessQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: HotFixChangeListener
 * @author: lex
 * @date: 2024-09-01
 **/
@Slf4j
@RequiredArgsConstructor
public class HotFixChangeListener implements SseDataStreamListener {

    private final HotfixProcessQueue hotfixProcessQueue;

    @Override
    public SseMsgIdEnum messageId() {
        return SseMsgIdEnum.HOTFIX_CHANGE;
    }

    @Override
    public void onMessage(String message) {
        log.info("HotFixChangeListener onMessage: {}", message);
        HotfixProcessParameter parameter = GsonFactory.fromJson(message, HotfixProcessParameter.class);
        hotfixProcessQueue.put(parameter);
    }

}
