package com.github.rodbate.datax.common.constant;

/**
 * User: rodbate
 * Date: 2018/12/17
 * Time: 16:29
 */
public final class RequestCodeConstants {

    private RequestCodeConstants() { }

    /**
     * agent心跳命令
     */
    public static final int AGENT_HEARTBEAT_CODE = 10;

    /**
     * core job 心跳命令
     */
    public static final int CORE_JOB_HEARTBEAT_CODE = 11;

    /**
     * 启动datax任务命令
     */
    public static final int START_DATAX_JOB_CODE = 20;

    /**
     * 关闭datax任务命令
     */
    public static final int KILL_DATAX_JOB_CODE = 21;

    /**
     * 注册datax任务命令
     */
    public static final int REGISTER_DATAX_JOB_CODE = 22;

    /**
     * datax任务上报命令
     */
    public static final int DATAX_JOB_REPORT_CODE = 23;
}
