package com.yang.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yang.util.exception.ConnectIsRunningException;
import com.yang.util.exception.ExecutorFullException;
import com.yang.util.exception.NoConnectException;
import com.yang.util.interf.BluetoothDateCallBack;
import com.yang.util.interf.BluetoothSearchCallBack;
import com.yang.util.interf.BluetoothSwitchCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/6/6.
 * 对内Handler，接收蓝牙的数据
 */

class DefaultHandler extends Handler{

    BluetoothSearchCallBack bluetoothSDKSearchCallBack;

    public BluetoothDateCallBack bluetoothDateCallBack;

    BluetoothSwitchCallBack bluetoothSwitchCallBack;

    Map<String, Object> map;

    @Override
    public synchronized void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            Log.e("what:", ":" + what);
            switch (what) {
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_SWITCH_OPEN:
                    // 蓝牙打开 刷新
                    if (bluetoothSwitchCallBack != null)
                        bluetoothSwitchCallBack.bluetoothSwitch(true);
                    DefaultGlobalConstants.bluetoothSwitch = true;
                    break;
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_SWITCH_CLOSE:
                    // 蓝牙关闭 保护
                    if (bluetoothDateCallBack != null)
                        bluetoothDateCallBack.readBluetoothDate(new byte[]{}, false, new BlueInfo());

                    if (bluetoothSwitchCallBack != null) {
                        bluetoothSwitchCallBack.bluetoothSwitch(false);
                        bluetoothDateCallBack.getThreadList(getBlueInfoList());
                    }
                    DefaultGlobalConstants.bluetoothSwitch = false;
                    break;
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_SEARCH_OPEN:
                    // 搜索开始
                    BlueInfo blueInfo = (BlueInfo) msg.obj;
                    if (bluetoothSDKSearchCallBack != null)
                        bluetoothSDKSearchCallBack.searchStateAndDate(true, blueInfo);
                    break;
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_SEARCH_CLOSE:
                    // 搜索关闭
                    if (bluetoothSDKSearchCallBack != null)
                        bluetoothSDKSearchCallBack.searchStateAndDate(false, new BlueInfo());
                    break;
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_SUCCESS:
                    // 连接成功
                    DefaultThread threadS = (DefaultThread) msg.obj;
                    // 添加至线程列表中
                    try {
                        DefaultThreadList.getInstance().add(threadS);
                        if (bluetoothDateCallBack != null) {
                            bluetoothDateCallBack.bluetoothConnectCallBack(true, threadS.blueInfo);
                            bluetoothDateCallBack.getThreadList(getBlueInfoList());
                        }
                    } catch (ExecutorFullException e) {
                        e.printStackTrace();
                        if (bluetoothDateCallBack != null) {
                            bluetoothDateCallBack.bluetoothConnectCallBack(false, threadS.blueInfo);
                            bluetoothDateCallBack.getThreadList(getBlueInfoList());
                        }
                    } catch (ConnectIsRunningException e) {
                        e.printStackTrace();
                        if (bluetoothDateCallBack != null) {
                            bluetoothDateCallBack.bluetoothConnectCallBack(false, threadS.blueInfo);
                            bluetoothDateCallBack.getThreadList(getBlueInfoList());
                        }
                    }
                    break;
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_FAIL:
                    // 连接失败
                    DefaultThread threadF = (DefaultThread) msg.obj;
                    if (bluetoothDateCallBack != null) {
                        bluetoothDateCallBack.bluetoothConnectCallBack(false, threadF.blueInfo);
                        bluetoothDateCallBack.getThreadList(getBlueInfoList());
                    }
                    break;
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_BREAK:
                    // 连接断开
                    DefaultThread threadB = (DefaultThread) msg.obj;
                    // 从线程列表中删除
                    try {
                        DefaultThreadList.getInstance().removeAndClose(threadB);
                    } catch (NoConnectException e) {
                        e.printStackTrace();
                    }
                    if (bluetoothDateCallBack != null) {
                        bluetoothDateCallBack.readBluetoothDate(new byte[]{}, false, threadB.blueInfo);
                        bluetoothDateCallBack.getThreadList(getBlueInfoList());
                    }

                    break;
                case DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_READ:
                    // 读取数据
                    map = (Map<String, Object>) msg.obj;
                    byte[] b = (byte[]) map.get(DefaultGlobalConstants.Content.BYTE_STRING);
                    BlueInfo blueInfo1 = (BlueInfo) map.get(DefaultGlobalConstants.Content.BLUEINFO_STRING);
                    if (bluetoothDateCallBack != null)
                        bluetoothDateCallBack.readBluetoothDate(b, true, blueInfo1);
                    break;
            }
    }

    public void setBluetoothSearchCallBack(BluetoothSearchCallBack bluetoothSDKSearchCallBack) {
        this.bluetoothSDKSearchCallBack = bluetoothSDKSearchCallBack;
    }

    public void setBluetoothDateCallBack(BluetoothDateCallBack bluetoothDateCallBack) {
        this.bluetoothDateCallBack = bluetoothDateCallBack;
    }

    public void setBluetoothSwitchCallBack(BluetoothSwitchCallBack bluetoothSwitchCallBack) {
        this.bluetoothSwitchCallBack = bluetoothSwitchCallBack;
    }

    // 得到蓝牙列表
    public List<BlueInfo> getBlueInfoList () {
        List<BlueInfo> blueInfoList = new ArrayList<>();

        Log.d("size:", DefaultThreadList.getInstance().getList().size()+"");

        BlueInfo blueInfo;
        for (DefaultThread thread : DefaultThreadList.getInstance().getList()) {
            blueInfo = new BlueInfo();
            blueInfo.address = thread.blueInfo.getAddress();
            blueInfo.name = thread.blueInfo.getName();
            blueInfoList.add(blueInfo);
        }

        return  blueInfoList;
    }

}