package com.github.rodbate.datax.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * User: rodbate
 * Date: 2019/3/7
 * Time: 15:06
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "datax")
public class DataXProperties {
    private Transport transport;
    private Job job;

    @Getter
    @Setter
    public static class Transport {
        private String host;
        private int port;
    }

    @Getter
    @Setter
    public static class Job {
        private String reportUrl;
    }
}
