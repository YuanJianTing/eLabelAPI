# eLabelAPI
- eLable Bluetooth electronic price tag Open API
-支持Android SDK5.0 以上版本；手机需要支持低功耗蓝牙(BLE)蓝牙版本为5.0以上

## Build 1.0.0
## 以上代码仅供对接参考,部分代码可能存在问题，请仔细阅读代码后在使用。
**使用流程**
- 初始化SDK(Android6.0需提前获取蓝牙及位置权限)
```java
BleManager.getInstance().init(context);
```
- 检查设备是否支持低功耗蓝牙
```java
boolean result= BleManager.getInstance().isSupportBle();
```
- 检查蓝牙是否开启（Android6.0以上版本需要开启位置服务才能扫描到周围设备）
```java
boolean result= BleManager.getInstance().isBlueEnable();
```
- 开启蓝牙
```java
//执行次方法后需要等待3~5秒，在进行其他蓝牙操作
BleManager.getInstance().enableBluetooth();
```
## 更新日志
- 1.0.0 上传代码


## 异常处理与解释
- 无法扫描到附近的设备
#### 重启应用或重新开关蓝牙再试；
- 总是提示蓝牙连接失败
#### 可能原因：1.多试几次； 2. 上次连接成功后未正确关闭；3.mac地址错误；4.标签距离过大，建议距离在5米范围内；
#### 5.检查标签电量是否过低；

- BleAdapterUninitializedException
#### 连接蓝牙时BleManager 尚未初始化；需要些调用BleManager.getInstance().init(context) 方法
- BleNotConnectedException 
#### 发送图片时，蓝牙尚未连接成功；需要些调用connect方法并回调onOpenNotifySuccess 后，在进行操作
- BleWriteException 
#### 发送图片过程中出现异常；建议检查图片格式尺寸是否正确，并多试几次
