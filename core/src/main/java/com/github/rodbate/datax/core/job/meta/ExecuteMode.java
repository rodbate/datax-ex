package com.github.rodbate.datax.core.job.meta;

/**
 * Created by liupeng on 15/12/21.
 */
public enum ExecuteMode {
    /**
     * execute
     */
    STANDALONE("standalone");

    final String value;

    ExecuteMode(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public String getValue() {
        return this.value;
    }
}
