package com.yang.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

/**
 * 初始化包
 * Created by yang on 2017/6/6.
 */

public class BluetoothInit {

    public static void init(Context context, int threadMaxNumber) {

        DefaultGlobalConstants.context = context;
        // 初始化handler
        if (DefaultGlobalConstants.defaultHandler == null)
            DefaultGlobalConstants.defaultHandler = new DefaultHandler();
        DefaultBluetoothUtil.isBluetootoReadly();

        // 初始化线程池大小
        ExecutorsFactory.getInstance(threadMaxNumber);

        // 初始化list集合
        DefaultThreadList.init(threadMaxNumber);
    }

    // 绑定广播
    public static void registerBroadcaseRecevier() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        // 初始化广播
        if (DefaultGlobalConstants.defaultBroadcaseReceiver == null)
            DefaultGlobalConstants.defaultBroadcaseReceiver = new DefaultBroadcaseReceiver();

        // 注册蓝牙开关
        DefaultGlobalConstants.context.registerReceiver(DefaultGlobalConstants.defaultBroadcaseReceiver, filter);
        // 扫描蓝牙开始
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        DefaultGlobalConstants.context.registerReceiver(DefaultGlobalConstants.defaultBroadcaseReceiver, filter);
        // 扫描蓝牙结束
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        DefaultGlobalConstants.context.registerReceiver(DefaultGlobalConstants.defaultBroadcaseReceiver, filter);
    }

    // 解除绑定
    public static void unRegisterBroadcaseRecevier() {
        // 关闭线程及线程池
        BluetoothUtil.closeOver();
        // 解除注册广播接收者
        DefaultGlobalConstants.context.unregisterReceiver(DefaultGlobalConstants.defaultBroadcaseReceiver);
    }
}
