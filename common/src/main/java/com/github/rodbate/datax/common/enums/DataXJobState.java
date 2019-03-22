package com.github.rodbate.datax.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 18:07
 */
public enum DataXJobState {

    SUBMITTING(10),
    WAITING(20),
    RUNNING(30),
    KILLING(40),
    KILLED(50),
    FAILED(60),
    SUCCEEDED(70);

    @Getter
    private final int state;

    DataXJobState(int state) {
        this.state = state;
    }


    public boolean isFinished() {
        return this == KILLED || this == FAILED || this == SUCCEEDED;
    }

    public boolean isRunning() {
        return !isFinished();
    }


    public static DataXJobState fromState(int state) {
        return Arrays.stream(values()).filter(t -> t.state == state).findAny().orElseThrow(() -> new IllegalArgumentException("Invalid job state: " + state));
    }
}
