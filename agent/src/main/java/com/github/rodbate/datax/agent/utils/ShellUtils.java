package com.github.rodbate.datax.agent.utils;

import com.github.rodbate.datax.agent.common.Constants;
import com.github.rodbate.datax.agent.config.ApplicationConfig;
import com.github.rodbate.datax.agent.exceptions.ShellExecuteException;
import com.github.rodbate.datax.common.enums.DataXExecuteMode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 16:21
 */
@Slf4j
public final class ShellUtils {


    public static void startDataXJob(DataXExecuteMode mode, long jobId, String jobConfUrl) {
        String cmd = buildDataXStartShellCommand(mode, jobId, jobConfUrl);

        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
            process.waitFor(5, TimeUnit.SECONDS);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ShellExecuteException(String.format("failed to execute command: %s, cause: %s", cmd, e.getMessage()), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new ShellExecuteException(String.format("shell execute command: %s interrupted, cause: %s", cmd, e.getMessage()), e);
        }
    }


    private static String buildDataXStartShellCommand(DataXExecuteMode mode, long jobId, String jobConfUrl) {
        if (mode == null) {
            mode = DataXExecuteMode.STANDALONE;
        }
        return String.format(Constants.DATAX_JOB_START_SHELL_TEMPLATE, getDataXExecFilePath(), mode.getMode(), jobId, jobConfUrl);
    }


    private static String getDataXExecFilePath() {
        return ApplicationConfig.getConfig().getDataxHome() + File.separator + "bin" + File.separator + "datax.py";
    }

}
