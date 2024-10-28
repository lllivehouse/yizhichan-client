package tech.yizhichan.client.core.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: FunctionThreadPoolExecutor
 * @author: lex
 * @date: 2024-08-19
 **/
@Slf4j
public final class FunctionThreadPoolExecutorFactory implements StandardThreadPoolExecutorFactory {
    private static volatile FunctionThreadPoolExecutorFactory instance;
    private static ThreadPoolExecutor threadPoolExecutor;
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private CountDownLatch latch = new CountDownLatch(1);

    private FunctionThreadPoolExecutorFactory(int corePoolSize, int maximumPoolSize) {
        if (!this.initialized.compareAndSet(false, true)) {
            try {
                this.latch.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException var5) {
                throw new RuntimeException("FunctionThreadPoolExecutorFactory Init Failed");
            }
        } else {
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), r -> {
                Thread thread = new Thread(r);
                thread.setName("function-worker-" + new Random().nextInt(100));
                return thread;
            }, new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }

    public static ThreadPoolExecutor create() {
        if (instance == null) {
            synchronized (FunctionThreadPoolExecutorFactory.class) {
                if (instance == null) {
                    int corePoolSize = Runtime.getRuntime().availableProcessors();
                    Integer maxPoolSize = Integer.valueOf(String.valueOf(Math.pow(corePoolSize, 2)));
                    instance = new FunctionThreadPoolExecutorFactory(corePoolSize, maxPoolSize);
                }
            }
        }
        return threadPoolExecutor;
    }

    @Override
    public void close() {
        instance = null;
        if (this.initialized.compareAndSet(true, false)) {
            shutdownThreadPoolGracefully(threadPoolExecutor, log);
        }
    }
}
