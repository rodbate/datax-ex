package com.github.rodbate.datax.common.web;

import lombok.Getter;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:38
 */
public enum ReturnCode {

    OK(0, "success"),

    BAD_REQUEST(400, "bad request"),

    NOT_FOUND(404, "not found"),

    INTERNAL_SERVER_ERROR(500, "internal server error"),

    APPLICATION_SHUTDOWN(510, "application shutdown now"),

    SYSTEM_BUSY(550, "system busy, service {0}"),

    NO_ALIVE_DATAX_AGENT(1000, "no alive datax agent"),

    JOB_IS_RUNNING(1001, "datax job(jobId={0}) is running"),

    JOB_IS_KILLING(1002, "datax job(jobId={0}) is killing"),

    NOT_FOUND_JOB(1003, "not found job(jobId={0})"),


    ;

    @Getter
    private final int code;
    @Getter
    private final String defaultMsg;

    ReturnCode(int code, String defaultMsg) {
        this.code = code;
        this.defaultMsg = defaultMsg;
    }


}
