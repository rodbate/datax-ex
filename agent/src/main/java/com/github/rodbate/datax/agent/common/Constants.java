package com.github.rodbate.datax.agent.common;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 15:07
 */
public final class Constants {

    public static final String DATAX_JOB_START_SHELL_TEMPLATE = "nohup python %s --mode %s --jobid %d %s > /dev/null 2>&1 &";


    private Constants() {
    }

}
