package com.github.rodbate.datax.common.service;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 15:27
 */
public interface IService {

    /**
     * start service
     */
    void start();

    /**
     * shutdown service
     *
     * @param shutdownNow whether shutdown now or not
     */
    void shutdown(boolean shutdownNow);


    default void shutdown() {
        shutdown(false);
    }

    static void closeExecutorService(ExecutorService executorService, boolean shutdownNow, Logger log) {
        Objects.requireNonNull(executorService, "executorService");
        if (shutdownNow) {
            executorService.shutdownNow();
        } else {
            executorService.shutdown();
            try {
                boolean terminated = executorService.awaitTermination(30, TimeUnit.SECONDS);
                if (!terminated) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                if (log != null) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
