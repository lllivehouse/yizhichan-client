package tech.yizhichan.client.core.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: BeanQueueThreadPoolExecutorFactory
 * @author: lex
 * @date: 2024-09-25
 **/
@Slf4j
public final class BeanQueueThreadPoolExecutorFactory implements StandardThreadPoolExecutorFactory {
    private static ExecutorService threadPoolExecutor;

    public BeanQueueThreadPoolExecutorFactory() {
        threadPoolExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("bean-worker");
            return thread;
        });
    }

    public static ExecutorService create() {
        if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
            new BeanQueueThreadPoolExecutorFactory();
        }
        return threadPoolExecutor;
    }

    @Override
    public void close() {
        if (threadPoolExecutor != null) {
            shutdownThreadPoolGracefully(threadPoolExecutor, log);
        }
    }
}
