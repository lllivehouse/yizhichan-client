package tech.yizhichan.client.core.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: FunctionThreadPoolExecutor
 * @author: lex
 * @date: 2024-08-19
 **/
@Slf4j
public final class HotfixQueueThreadPoolExecutorFactory implements StandardThreadPoolExecutorFactory {
    private static ExecutorService threadPoolExecutor;

    private HotfixQueueThreadPoolExecutorFactory() {
        threadPoolExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("hotfix-worker");
            return thread;
        });
    }

    public static ExecutorService create() {
        if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
            new HotfixQueueThreadPoolExecutorFactory();
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
