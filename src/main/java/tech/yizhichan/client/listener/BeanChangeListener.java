package tech.yizhichan.client.listener;

import tech.zhizheng.common.model.sse.SseMsgIdEnum;
import tech.zhizheng.common.utils.GsonFactory;
import tech.yizhichan.client.queue.BeanProcessParameter;
import tech.yizhichan.client.queue.BeanProcessQueue;
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
