package com.yang.util.interf;

import com.yang.util.BlueInfo;

import java.util.List;

/**
 * Created by shiq_yang on 2017/6/8.
 * 蓝牙连接及数据读取接口
 */

public interface BluetoothDateCallBack {
    // 蓝牙连接状态
    void bluetoothConnectCallBack(boolean flag, BlueInfo blueInfo);

    // 数据读取
    void readBluetoothDate(byte[] bytes, boolean flag, BlueInfo blueInfo);

    // 连接列表
    void getThreadList(List<BlueInfo> blueInfoList);

}
