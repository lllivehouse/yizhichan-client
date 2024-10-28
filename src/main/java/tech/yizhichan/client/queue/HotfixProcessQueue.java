package tech.yizhichan.client.queue;

import tech.zhizheng.common.utils.queue.DisruptorQueue;
import tech.zhizheng.common.utils.queue.Queueable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;

/**
 * @author lex
 * @createTime 2024/8/31
 * @description HotfixProcessQueue
 */
@RequiredArgsConstructor
public class HotfixProcessQueue<T> implements Queueable<T>, InitializingBean, DisposableBean {
    private final HotfixQueueConsumer hotfixQueueConsumer;
    private final ExecutorService executorService;
    private DisruptorQueue<T> queue;

    @Override
    public void afterPropertiesSet() {
        // buffer size:131072
        this.queue = (DisruptorQueue<T>) new DisruptorQueue<>(2 << 17, false, executorService, hotfixQueueConsumer);
    }

    @Override
    public void put(T t) {
        this.queue.add(t);
    }

    @Override
    public void close() {
        this.queue.shutdown();
    }

    @Override
    public void destroy() throws Exception {
        close();
    }
}
