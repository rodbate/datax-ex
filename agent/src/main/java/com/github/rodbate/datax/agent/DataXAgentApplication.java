package com.github.rodbate.datax.agent;

import com.alibaba.fastjson.JSON;
import com.github.rodbate.datax.agent.common.ApplicationManager;
import com.github.rodbate.datax.agent.config.ApplicationConfig;
import com.github.rodbate.datax.common.ApplicationMain;
import com.github.rodbate.datax.common.config.PropertiesConfigLoader;
import com.github.rodbate.datax.common.constant.CommonConstant;
import com.github.rodbate.datax.common.util.LogbackUtil;
import com.github.rodbate.datax.common.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * datax agent 程序入口
 * <p>
 * User: rodbate
 * Date: 2019/3/5
 * Time: 10:50
 */
@Slf4j
public class DataXAgentApplication {

    private static final String APPLICATION_PROPERTIES_FILE_PATH = "application.properties";

    /**
     * 主程序入口
     *
     * @param args args
     */
    public static void main(String[] args) {
        Properties properties = PropertiesUtil.load(APPLICATION_PROPERTIES_FILE_PATH, DataXAgentApplication.class.getClassLoader());
        String logConfigFilePath = properties.getProperty(CommonConstant.LOG_CONFIG_FILE_KEY);
        if (StringUtils.isBlank(logConfigFilePath)) {
            throw new IllegalStateException(String.format("property %s require not null", CommonConstant.LOG_CONFIG_FILE_KEY));
        }
        LogbackUtil.initConfiguration(logConfigFilePath);
        ApplicationConfig config = new PropertiesConfigLoader().loadConfig(properties, ApplicationConfig.class);
        log.info("Application Config Values: \n {}", JSON.toJSONString(config, true));
        ApplicationConfig.bind(config);

        final ApplicationManager applicationManager = new ApplicationManager();
        ApplicationMain.startUp(DataXAgentApplication.class.getSimpleName(), null, applicationManager::start, applicationManager::shutdown);
    }

}
