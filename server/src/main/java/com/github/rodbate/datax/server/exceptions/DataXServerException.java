package com.github.rodbate.datax.server.exceptions;

import com.github.rodbate.datax.common.web.ReturnCode;
import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:43
 */
@Getter
@Setter
public class DataXServerException extends RuntimeException {

    private final ReturnCode returnCode;
    private final Object[] args;

    public DataXServerException(ReturnCode returnCode, Object... args) {
        super();
        this.returnCode = returnCode;
        this.args = args;
    }

    public DataXServerException(ReturnCode returnCode, Throwable ex, Object... args) {
        super(ex);
        this.returnCode = returnCode;
        this.args = args;
    }

    private String formatMessage(String message, Object[] args) {
        if (args != null && args.length > 0) {
            message = MessageFormat.format(message, args);
        }
        return message;
    }

    @Override
    public String getMessage() {
        return formatMessage(returnCode.getDefaultMsg(), args);
    }
}
