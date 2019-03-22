package com.github.rodbate.datax.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主程序入口
 *
 * User: rodbate
 * Date: 2019/3/5
 * Time: 11:52
 */
@SpringBootApplication
public class DataXServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataXServerApplication.class, args).registerShutdownHook();
    }
}
