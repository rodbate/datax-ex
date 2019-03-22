package com.github.rodbate.datax.server.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.ref.WeakReference;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 15:52
 */
@Slf4j
public class BootApplicationRunListener implements SpringApplicationRunListener {

    public static volatile int webServerPort;

    private final WeakReference<SpringApplication> springApplicationRef;
    private final String[] args;


    public BootApplicationRunListener(SpringApplication application, String[] args) {
        this.springApplicationRef = new WeakReference<>(application);
        this.args = args;
    }

    @Override
    public void starting() {
        log.info("Application Starting");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        log.info("Application EnvironmentPrepared => Application Env: {}, command args: {}", environment.getProperty("app.env"), args);
        webServerPort = Integer.valueOf(environment.getProperty("server.port"));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        log.info("Application ContextPrepared");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.info("Application ContextLoaded");
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        log.info("Application Started");
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        log.info("Application Running");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.error("Application Failed", exception);
    }


}
