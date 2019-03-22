package com.github.rodbate.datax.agent.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 10:53
 */
@Slf4j
@Getter
@Setter
public class ApplicationConfig {
    private static ApplicationConfig config = null;
    private final static AtomicBoolean FROZEN = new AtomicBoolean(false);

    private String logConfigFile;
    private String dataxServerAddress;
    private String dataxHome;


    public static void bind(ApplicationConfig config) {
        if (FROZEN.compareAndSet(false, true)) {
            ApplicationConfig.config = config;
        } else {
            throw new IllegalStateException("ApplicationConfig has already bound");
        }
    }

    public static ApplicationConfig getConfig() {
        return ApplicationConfig.config;
    }

}
