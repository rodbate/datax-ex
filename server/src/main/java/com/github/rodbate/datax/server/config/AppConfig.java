package com.github.rodbate.datax.server.config;

import com.github.rodbate.datax.server.DataXServerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 12:00
 */
@Configuration
public class AppConfig {


    @Bean
    public DataXServerManager dataXServerController(DataXProperties dataXProperties) {
        return new DataXServerManager(dataXProperties);
    }

}
