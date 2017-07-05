package com.yang.util;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.yang.util.exception.BluetoothSwitchCloseException;
import com.yang.util.exception.ConnectIsRunningException;
import com.yang.util.exception.ExecutorFullException;
import com.yang.util.exception.InputIncompleteException;
import com.yang.util.exception.NoConnectException;
import com.yang.util.interf.BluetoothDateCallBack;
import com.yang.util.interf.BluetoothSearchCallBack;
import com.yang.util.interf.BluetoothSwitchCallBack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by shiq_yang on 2017/6/8.
 * 对外提供的功能接口
 */

public class BluetoothUtil {

    // 监听蓝牙开关
    public static void listenerBluetoothSwitch(BluetoothSwitchCallBack bluetoothSwitchCallBack) {
        DefaultGlobalConstants.defaultHandler.setBluetoothSwitchCallBack(bluetoothSwitchCallBack);
    }

    // 得到蓝牙一配对过的对象
    public static List<BlueInfo> getBondedDevices() {
        List<BlueInfo> list = new ArrayList<BlueInfo>();
        // 确保打开蓝牙功能
        if (DefaultBluetoothUtil.isBluetootoReadly()) {

            Set<BluetoothDevice> pairedDevices = DefaultBluetoothUtil
                    .getBluetoothAdapter().getBondedDevices();
            if (pairedDevices.size() > 0) {
                BlueInfo info = null;
                for (BluetoothDevice device : pairedDevices) {
                    info = new BlueInfo();
                    info.name = TextUtils.isEmpty(device.getName()) ? "未知蓝牙"
                            : device.getName();
                    info.address = device.getAddress();
                    list.add(info);
                }
            }
        }
        return list;
    }

    // 打开蓝牙搜索
    public static void openSearchBluetooth(BluetoothSearchCallBack bluetoothSDKSearchCallBack) {
        if (DefaultBluetoothUtil.getBluetoothAdapter().isDiscovering())
            closeSearchBluetooth();
        DefaultBluetoothUtil.getBluetoothAdapter().startDiscovery();
        DefaultGlobalConstants.defaultHandler.setBluetoothSearchCallBack(bluetoothSDKSearchCallBack);
    }

    // 关闭蓝牙搜索
    public static void closeSearchBluetooth() {
        DefaultBluetoothUtil.getBluetoothAdapter().cancelDiscovery();
    }

    // 开启连接
    public static void connect(BlueInfo blueInfo, BluetoothDateCallBack bluetoothDateCallBack) throws ExecutorFullException, BluetoothSwitchCloseException, InputIncompleteException, ConnectIsRunningException {
        // 蓝牙功能未打开
        if (!DefaultBluetoothUtil.isBluetootoReadly())
            throw new BluetoothSwitchCloseException();

        if (blueInfo == null && TextUtils.isEmpty(blueInfo.address) && TextUtils.isEmpty(blueInfo.name))
            throw new InputIncompleteException();

        // 确认线程是否在队列
        if (DefaultThreadList.getInstance().getThread(blueInfo) != null)
            throw new ConnectIsRunningException();

        // 线程集合是否已满
        if (DefaultThreadList.getInstance().isFull()) {
            throw new ExecutorFullException();
        }

        // 得到相应蓝牙
        BluetoothDevice device = DefaultBluetoothUtil.getBluetoothAdapter().getRemoteDevice(blueInfo.address);

        // 开启线程
        if (DefaultGlobalConstants.defaultHandler.bluetoothDateCallBack == null)
            DefaultGlobalConstants.defaultHandler.setBluetoothDateCallBack(bluetoothDateCallBack);
        DefaultExecutorsFactory.getInstance().addThread(new DefaultThread(device, blueInfo));

    }

    // 写内容
    public static void write(String string, BlueInfo blueInfo) throws IOException {
        // 找到线程
        for (DefaultThread thread :
                DefaultThreadList.getInstance().getList()) {
            if (thread.blueInfo.getAddress().equals(blueInfo.getAddress())) {
                // 写数据
                thread.write(string);
            }
        }
    }

    // 关闭单个连接
    public static void closeBluetoothConnect(BlueInfo blueInfo) throws NoConnectException {
        DefaultThreadList.getInstance().removeAndClose(blueInfo);
    }

    // 关闭连接
    public static void closeOver() {
        DefaultThreadList.getInstance().removeAll();
        DefaultExecutorsFactory.getInstance().close();
    }

}
