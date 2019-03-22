package com.github.rodbate.datax.common.enums;

import lombok.Getter;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 16:46
 */
public enum DataXExecuteMode {
    /**
     *
     */
    STANDALONE("standalone"),
    LOCAL("local"),
    DISTRIBUTE("distribute");

    @Getter
    private final String mode;

    DataXExecuteMode(String mode) {
        this.mode = mode;
    }

}
