package com.yang.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shiq_yang on 2017/6/29.
 * 线程池
 */

class ExecutorsFactory {

    private ExecutorService executorService;
    private static ExecutorsFactory executorsFactory;
    private static int maxSize;

    private ExecutorsFactory(int maxSize) {
        ExecutorsFactory.maxSize = maxSize;
        executorService = Executors.newFixedThreadPool(maxSize);
    }

    public static ExecutorsFactory getInstance() {
        synchronized (ExecutorsFactory.class) {
            if (executorsFactory == null)
                executorsFactory = new ExecutorsFactory(maxSize);
        }
        return executorsFactory;
    }

    public static ExecutorsFactory getInstance(int maxSize) {
        synchronized (ExecutorsFactory.class) {
            if (executorsFactory == null)
                executorsFactory = new ExecutorsFactory(maxSize);
        }
        return executorsFactory;
    }

    // 开启线程
    public void addThread(DefaultThread thread) {

        // 开启线程
        executorService.execute(thread);
    }

    // 关闭线程池
    public void close() {
        executorService.shutdownNow();
    }

}
