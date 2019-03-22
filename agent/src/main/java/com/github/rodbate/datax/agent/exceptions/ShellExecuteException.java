package com.github.rodbate.datax.agent.exceptions;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 16:56
 */
public class ShellExecuteException extends RuntimeException {

    public ShellExecuteException() {
        super();
    }

    public ShellExecuteException(String message) {
        super(message);
    }

    public ShellExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellExecuteException(Throwable cause) {
        super(cause);
    }
}
