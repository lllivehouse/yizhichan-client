package tech.yizhichan.sdk.core.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description: StandardThreadPoolExecutorFactory
 * @author: lex
 * @date: 2024-08-22
 **/
public interface StandardThreadPoolExecutorFactory extends AutoCloseable {

    /**
     * 优雅关闭线程池
     *
     * @param threadPool
     * @param log
     */
    default void shutdownThreadPoolGracefully(ExecutorService threadPool, org.slf4j.Logger log) {
        log.info("Start to shutdown function thead pool");
        // 使新任务无法提交.
        threadPool.shutdown();
        try {
            // 等待未完成任务结束
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                // 取消当前执行的任务
                threadPool.shutdownNow();
                log.warn("Interrupt the worker, which may cause some task inconsistent. Please check the biz logs.");

                // 等待任务取消的响应
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Thread pool can't be shutdown even with interrupting worker threads, which may cause some task inconsistent. Please check the logs.");
                }
            }
        } catch (InterruptedException ie) {
            // 重新取消当前线程进行中断
            threadPool.shutdownNow();
            log.error("The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconsistent state. Please check the logs.");

            // 保留中断状态
            Thread.currentThread().interrupt();
        }

        log.info("Finally shutdown the function thead pool");
    }
}
