BluetoothHandlerPlus（非低频蓝牙RFCOMM通信）（可连接多台设备并进行通信）
===================================
说明：用于连接设备蓝牙并发送或接收AT指令,其中已添加功能权限和蓝牙状态等广播接收，只需在回调接口中处理即可，该连接可一对多设备连接并数据传递.


这里还有用于单连接的应用的蓝牙RFCOMM通信依赖
-----------------------------------
[https://github.com/Yangandmore/BluetoothHandler](https://github.com/Yangandmore/BluetoothHandler)


在项目的build文件下添加依赖并Make
-----------------------------------
```groovy
        allprojects {
            repositories {
                    ...
                    maven { url 'https://jitpack.io'} // 添加项
                }
        }


        dependencies {
            ...
            compile 'com.github.Yangandmore:BluetoothHandlerPlus:V2.0' // 添加项
        }
```


### 版本说明:
    目前版本：
        V2.0:增加 try-catch 功能
    旧版:
        V1.0:基本功能



### 1.在Application中主功能初始化
```Java
        public class XXX extends Application {

            @Override
            public void onCreate() {
                super.onCreate();
                // 初始化需要this和连接个数
                BluetoothInit.init(this, 3);
            }
        }
```

### 2.在需要做蓝牙功能的地方做好蓝牙初始化及相应的蓝牙安全关闭

        ...
```Java
        private void init() {
               // 蓝牙功能初始化
               BluetoothInit.registerBroadcaseRecevier();
        }

        ...

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // 蓝牙功能安全关闭
            BluetoothInit.unRegisterBroadcaseRecevier();
        }
```

### 3.可以在项目中添加监听器已监听蓝牙是否打开或关闭.
```Java
        BluetoothUtil.listenerBluetoothSwitch(new BluetoothSwitchCallBack() {
            @Override
            public void bluetoothSwitch(boolean flag) {
                if (flag) {
                    Toast.makeText(MainActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });
```

### 4.可以得到手机已配对过的蓝牙对象列表。也在需要搜索的地方开启蓝牙搜索及蓝牙搜索关闭的地方添加相应功能,在搜索开启状态返回true的同时也会返回相应的蓝牙对象,其中包含他的名字和地址.当然也可以手动关闭蓝牙搜索功能.
```Java
        // 得到手机已配对过的蓝牙对象列表
        try {
            topList.addAll(BluetoothUtil.getBondedDevices());
        } catch (BluetoothSupportedException e) {
            e.printStackTrace();
        } catch (BluetoothSwitchCloseException e) {
            e.printStackTrace();
        }
```

```Java
        // 蓝牙的搜索及蓝牙搜索结束的状态回调
        try {
            BluetoothUtil.openSearchBluetooth(new BluetoothSearchCallBack() {
                @Override
                    public void searchStateAndDate(boolean flag, BlueInfo blueInfo) {
                        if (flag) {
                            // 得到搜索的蓝牙对象，并进行处理
                             bottomList.add(blueInfo);
                             bottomAdapter.notifyDataSetChanged();
                        } else {
                             Toast.makeText(ListActivity.this, "搜索结束", Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        } catch (BluetoothSupportedException e) {
            e.printStackTrace();
            // 设备不支持蓝牙功能
        } catch (BluetoothSwitchCloseException e) {
            e.printStackTrace();
            // 蓝牙功能未打开
        }
```

```Java
        // 关闭搜索功能
        closeSearchBluetooth();
```

### 5.开始对蓝牙的连接通信,返回的数据以原始16进制数据读取并传递,当然也可以手动断开蓝牙连接,一般情况下一定要及时关闭蓝牙连接.此时需要处理一些异常情况，如线程池已满、蓝牙关闭等.
```Java
        try {
            BluetoothUtil.connect(blueInfo, new BluetoothDateCallBack() {
                @Override
                public void bluetoothConnectCallBack(boolean flag, BlueInfo blueInfo1) {
                    // 连接状态
                    if (flag)
                        Toast.makeText(MainActivity.this, blueInfo1.getAddress() + "连接成功", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, blueInfo1.getAddress() + "连接失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void readBluetoothDate(byte[] bytes, boolean flag, BlueInfo blueInfo1) {
                    // 数据读取
                    if (flag) {
                        Toast.makeText(MainActivity.this, blueInfo1.getAddress() + "数据读取", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, blueInfo1.getAddress() + "连接断开", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void getThreadList(List<BlueInfo> blueInfoList) {
                    // 得到此时已连接的所有蓝牙对象

                    ...
                }
            });
        } catch (BluetoothSupportedException e) {
            e.printStackTrace();
            // 设备不支持蓝牙功能
        } catch (ExecutorFullException e) {
            e.printStackTrace();
            // 线程池已满
        } catch (BluetoothSwitchCloseException e) {
            e.printStackTrace();
            // 蓝牙功能未打开
        } catch (InputIncompleteException e) {
            e.printStackTrace();
            // 数据不全
        } catch (ConnectIsRunningException e) {
            e.printStackTrace();
            // 该连接已存在
        }
```

### 6.也可以使用手动关闭连接来断开连接，这里需要添加选择的蓝牙对象，并对其进行异常处理.并提供关闭所有连接的功能.
```Java
        try {
            // 关闭单连接
            BluetoothUtil.closeBluetoothConnect(blueInfoList.get(0));
        } catch (NoConnectException e) {
            // 未找到该连接
            e.printStackTrace();
        }
```

```Java
        // 关闭所有连接
        BluetoothUtil.closeOver();
```