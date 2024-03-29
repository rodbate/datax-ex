package com.github.rodbate.datax.plugin.writer.sqlserverwriter;

import com.github.rodbate.datax.common.spi.ErrorCode;

public enum SqlServerWriterErrorCode implements ErrorCode {
    ;

    private final String code;
    private final String describe;

    private SqlServerWriterErrorCode(String code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.describe;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Describe:[%s]. ", this.code,
                this.describe);
    }
}
