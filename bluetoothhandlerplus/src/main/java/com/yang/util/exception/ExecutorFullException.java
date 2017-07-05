package com.yang.util.exception;

/**
 * Created by shiq_yang on 2017/7/4.
 * 线程池过载异常
 */

public class ExecutorFullException extends Exception {

    public ExecutorFullException() {
        super("线程池已过载");
    }
}
