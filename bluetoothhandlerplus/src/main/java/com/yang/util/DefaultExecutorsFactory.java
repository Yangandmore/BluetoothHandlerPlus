package com.yang.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shiq_yang on 2017/6/29.
 * 线程池
 */

class DefaultExecutorsFactory {

    private ExecutorService executorService;
    private static DefaultExecutorsFactory executorsFactory;
    private static int maxSize;

    private DefaultExecutorsFactory(int maxSize) {
        DefaultExecutorsFactory.maxSize = maxSize;
        executorService = Executors.newFixedThreadPool(maxSize);
    }

    public static DefaultExecutorsFactory getInstance() {
        synchronized (DefaultExecutorsFactory.class) {
            if (executorsFactory == null)
                executorsFactory = new DefaultExecutorsFactory(maxSize);
        }
        return executorsFactory;
    }

    public static DefaultExecutorsFactory getInstance(int maxSize) {
        synchronized (DefaultExecutorsFactory.class) {
            if (executorsFactory == null)
                executorsFactory = new DefaultExecutorsFactory(maxSize);
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
