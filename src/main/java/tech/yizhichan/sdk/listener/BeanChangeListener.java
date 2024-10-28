package tech.yizhichan.sdk.listener;

import tech.yizhichan.common.model.sse.SseMsgIdEnum;
import tech.yizhichan.common.utils.GsonFactory;
import tech.yizhichan.sdk.queue.BeanProcessParameter;
import tech.yizhichan.sdk.queue.BeanProcessQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: BeanChangeListener
 * @author: lex
 * @date: 2024-09-25
 **/
@Slf4j
@RequiredArgsConstructor
public class BeanChangeListener implements SseDataStreamListener {

    private final BeanProcessQueue beanProcessQueue;

    @Override
    public SseMsgIdEnum messageId() {
        return SseMsgIdEnum.BEAN_CHANGE;
    }

    @Override
    public void onMessage(String message) {
        log.info("BeanChangeListener onMessage: {}", message);
        BeanProcessParameter parameter = GsonFactory.fromJson(message, BeanProcessParameter.class);
        beanProcessQueue.put(parameter);
    }

}
