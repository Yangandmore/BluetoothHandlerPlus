package com.yang.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by yang on 2017/6/6.
 * 对内线程，读取蓝牙数据
 */

class DefaultThread extends Thread {

    public BlueInfo blueInfo;
    private BluetoothDevice device;
    private BluetoothSocket bluetoothSocket;
    private InputStream is;
    private OutputStream os;
    private boolean flag = true;

    public DefaultThread(BluetoothDevice device, BlueInfo blueInfo) {
        this.device = device;
        this.blueInfo = blueInfo;
    }

    @Override
    public void run(){
        // 暂停蓝牙搜索
        BluetoothUtil.closeSearchBluetooth();

        // 开启蓝牙连接
        if (bluetoothSocket == null) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(DefaultGlobalConstants.UUID_SPP);
                bluetoothSocket.connect();
            } catch (IOException e) {
                try {
                    bluetoothSocket = (BluetoothSocket) device
                            .getClass()
                            .getMethod("createRfcommSocket",
                                    new Class[] { int.class }).invoke(device, 1);
                    bluetoothSocket.connect();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        //判断蓝牙连接是否成功
            if (bluetoothSocket.isConnected()) {
                if (flag) {
                    // 蓝牙配对成功
                    Message message = DefaultGlobalConstants.defaultHandler.obtainMessage(DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_SUCCESS);
                    message.obj = this;
                    DefaultGlobalConstants.defaultHandler.sendMessage(message);
                    flag = false;
                }
            } else {
                if (flag) {
                    // 蓝牙配对失败
                    Message message = DefaultGlobalConstants.defaultHandler.obtainMessage(DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_FAIL);
                    message.obj = this;
                    DefaultGlobalConstants.defaultHandler.sendMessage(message);
                    flag = false;
                    close();
                    return ;
                }
            }

            // 读取数据
            try {
                is = bluetoothSocket.getInputStream();
                os = bluetoothSocket.getOutputStream();
                int l = 0;
                Map<String, Object> map;
                byte[] buf = new byte[2048];
                while (true) {
                    if (bluetoothSocket.isConnected()) {
                        // 读取数据
                        l = is.read(buf);
                        if (l > 0) {
                            byte[] result = Arrays.copyOf(buf, l);
                            map = new HashMap<>();
                            Message message = DefaultGlobalConstants.defaultHandler.obtainMessage(DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_READ);
                            // 数据封装
                            map.put(DefaultGlobalConstants.Content.BLUEINFO_STRING, blueInfo);
                            map.put(DefaultGlobalConstants.Content.BYTE_STRING, result);
                            message.obj = map;
                            DefaultGlobalConstants.defaultHandler.sendMessage(message);
                        }

                    } else {
                        // 连接断开
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
                Message message = DefaultGlobalConstants.defaultHandler.obtainMessage(DefaultGlobalConstants.HandlerState.BLUETOOTH_CONNECT_BREAK);
                message.obj = this;
                DefaultGlobalConstants.defaultHandler.sendMessage(message);
            }
        }

    }

    // 写内容
    public void write(String string) throws IOException {
        // 转16进制写
        byte[] bytes = DefaultBluetoothUtil.hexStringToBytes(string);
        os.write(bytes);
    }

    // 关闭线程
    public void close() {
        if (bluetoothSocket != null)
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (is != null)
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (os != null)
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
