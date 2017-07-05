package com.yang.util;

import com.yang.util.exception.ConnectIsRunningException;
import com.yang.util.exception.ExecutorFullException;
import com.yang.util.exception.NoConnectException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiq_yang on 2017/7/4.
 */

class DefaultThreadList {

    private static DefaultThreadList defaultThreadLists;
    // 线程池映射集合
    private List<DefaultThread> defaultThreadList;
    // 最大容量
    private static int maxSize = 0;
    // 此时容量
    private static int currentSize = 0;

    // 初始化
    public static void init(int maxSize) {
        DefaultThreadList.maxSize = maxSize;
        defaultThreadLists = new DefaultThreadList();
    }

    private DefaultThreadList() {
        defaultThreadList = new ArrayList<>();
    }

    public static DefaultThreadList getInstance() {
        return defaultThreadLists;
    }

    // 得到当前list列表
    public List<DefaultThread> getList() {
        return defaultThreadList;
    }

    public boolean isFull() {
        if (DefaultThreadList.maxSize == currentSize)
            return true;
        else
            return false;
    }

    // 添加
    public void add(DefaultThread defaultThread) throws ExecutorFullException, ConnectIsRunningException {
        // 判断容量是否已满
        if (isFull())
            throw new ExecutorFullException();

        // 添加
        DefaultThread thread = getThread(defaultThread);
        if (thread == null) {
            defaultThreadList.add(defaultThread);
            currentSize++;

        } else {
            throw new ConnectIsRunningException();
        }
    }

    // 移除线程
    public void removeAndClose(DefaultThread defaultThread) throws NoConnectException {
        DefaultThread thread = getThread(defaultThread);
        if (thread == null) {
            throw new NoConnectException();
        } else {
            defaultThreadList.remove(thread);
            thread.close();
            currentSize--;
        }
    }

    public void removeAndClose(BlueInfo blueInfo) throws NoConnectException {
        DefaultThread thread = getThread(blueInfo);
        if (thread == null) {
            throw new NoConnectException();
        } else {
            defaultThreadList.remove(thread);
            thread.close();
            currentSize--;
        }
    }

    public void removeAll() {
        for (DefaultThread thread :
                defaultThreadList) {
            thread.close();
        }
        defaultThreadList.clear();
        currentSize = 0;
    }

    // 从list找到相应线程对象
    public DefaultThread getThread(BlueInfo blueInfo) {
        for (DefaultThread thread :
                defaultThreadList) {
            if (blueInfo.getAddress().equals(thread.blueInfo.getAddress())) {
                return thread;
            }
        }
        return null;
    }
    public DefaultThread getThread(DefaultThread defaultThread) {
        for (DefaultThread thread :
                defaultThreadList) {
            if (defaultThread.blueInfo.getAddress().equals(thread.blueInfo.getAddress())) {
                return thread;
            }
        }
        return null;
    }

}