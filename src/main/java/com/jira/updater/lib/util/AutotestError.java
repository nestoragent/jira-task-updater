package com.jira.updater.lib.util;

/**
 * Created by VelichkoAA on 14.01.2016.
 */
public class AutotestError extends AssertionError {

    public AutotestError(String message) {
        super(message);
    }

    public AutotestError(String message, Throwable cause) {
        super(message, cause);
    }

    public AutotestError(Throwable cause) {
        super(cause);
    }
}
