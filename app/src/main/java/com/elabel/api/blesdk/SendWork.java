package com.elabel.api.blesdk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.elabel.api.blesdk.exception.BleAdapterUninitializedException;
import com.elabel.api.blesdk.exception.BleNotConnectedException;
import com.elabel.api.blesdk.exception.BleWriteException;
import com.elabel.api.utils.StringUtils;
import com.elabel.api.utils.TimeoutHandler;

import java.util.List;
import java.util.UUID;

public class SendWork {
    public static int PACE=20;
    //发送超时时间
    private static final int Timeout = 1000*20;

    public static final UUID RX_SERVICE_UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123");//UART Sevice

    public static final UUID RX_CHAR_UUID = UUID.fromString("00001525-1212-efde-1523-785feabcd123");//写服务
    public static final UUID TX_CHAR_UUID = UUID.fromString("00001526-1212-efde-1523-785feabcd123");//读服务
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BleTask task;
    private final BluetoothAdapter mBluetoothAdapter;
    private OnSendingListener onSendingListener;
    private final Context context;
    private BluetoothGatt connectGatt;
    private TimeoutHandler timeoutHandler;
    private TimeoutHandler connectTimeoutHandler;
    private String mac;

//    private ScanSettings scanSettings;
//    private BluetoothLeScanner scanner;

    public SendWork(Context context,BleManager bleManager){
        this.context=context;
        mBluetoothAdapter=bleManager.getBluetoothAdapter();
        timeoutHandler=new TimeoutHandler();
        timeoutHandler.setTimeout(Timeout);
        timeoutHandler.setOnTimeoutListener(()->sendFail("接受反馈超时！"));
        connectTimeoutHandler=new TimeoutHandler();
        connectTimeoutHandler.setTimeout(1000*30);
        connectTimeoutHandler.setOnTimeoutListener(()->sendFail("连接蓝牙超时！"));

//        ScanSettings.Builder builder=new ScanSettings.Builder()
//                //设置高功耗模式
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
//        //android 6.0添加设置回调类型、匹配模式等
//        if(android.os.Build.VERSION.SDK_INT >= 23) {
//            //定义回调类型
//            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
//            //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
//            builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
//        }
//        //芯片组支持批处理芯片上的扫描
//        if (mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
//            //设置蓝牙LE扫描的报告延迟的时间（以毫秒为单位）
//            //设置为0以立即通知结果
//            builder.setReportDelay(0L);
//        }
//        scanSettings=builder.build();
    }

    public void send(BleTask task) {
        //task.setTaskType(10);

        task.setSendCount();
        this.task=task;
        Log.e("YUAN","开始发送："+task.getMac());
        String regex = "(.{2})";
        String mac = task.getMac().replaceAll(regex,"$1:");
        this.mac = mac.substring(0,mac.length() - 1);
//        scanner= mBluetoothAdapter.getBluetoothLeScanner();
//        List<ScanFilter> filters=new ArrayList<>();
//        ScanFilter scanFilter = new ScanFilter.Builder()
//                .setDeviceAddress(this.mac)
//                .setServiceUuid(ParcelUuid.fromString(RX_SERVICE_UUID.toString()))
//                .build();
//        filters.add(scanFilter);
//        scanner.startScan(filters, scanSettings,scanCallback);


        BluetoothDevice device=  mBluetoothAdapter.getRemoteDevice(this.mac);
        if(device==null){
            sendFail("无法搜索到标签");
            return;
        }
        connect(device);
    }

    @SuppressLint("MissingPermission")
    private void connect(BluetoothDevice device){
        connectTimeoutHandler.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectGatt = device.connectGatt(context,false, new TagBleConnectCallback(TX_CHAR_UUID){

                @Override
                protected void onConnectSuccess(BluetoothGatt gatt) {
                    connectTimeoutHandler.cancel();
                    boolean result= openNotify(gatt);
                    if(result)
                        sendTask();
                    else
                        sendFail("打开通知失败！");
                }

                @Override
                protected void onConnectFail() {
                    connectTimeoutHandler.cancel();
                    sendFail("连接蓝牙失败！");
                }

                @Override
                protected void onReceiveMessage(BluetoothGatt gatt, BleDeviceFeedback deviceFeedback) {
                    Log.e("YUAN","通信成功。"+deviceFeedback.getPower());
                    sendSuccess(deviceFeedback,task);
                }
            }, BluetoothDevice.TRANSPORT_LE);
        }else {
            connectGatt = device.connectGatt(context, false, new TagBleConnectCallback(TX_CHAR_UUID) {
                @Override
                protected void onConnectSuccess(BluetoothGatt gatt) {
                    connectTimeoutHandler.cancel();
                    boolean result = openNotify(gatt);
                    if(result)
                        sendTask();
                    else
                        sendFail("打开通知失败");
                }

                @Override
                protected void onConnectFail() {
                    connectTimeoutHandler.cancel();
                    sendFail("连接蓝牙失败");
                }

                @Override
                protected void onReceiveMessage(BluetoothGatt gatt, BleDeviceFeedback deviceFeedback) {
                    Log.e("YUAN","通信成功。"+deviceFeedback.getPower());
                    sendSuccess(deviceFeedback,task);
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private boolean openNotify(BluetoothGatt gatt){
        BluetoothGattService RxService = gatt.getService(RX_SERVICE_UUID);
        if(RxService==null)
            return false;
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);//读
        if(TxChar==null)
            return false;
        boolean result=  gatt.setCharacteristicNotification(TxChar,true);
        Log.e("ETAG","打开通知状态1："+result);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
        result=descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        Log.e("ETAG","打开通知状态2："+result);
        result= gatt.writeDescriptor(descriptor);
        Log.e("ETAG","打开通知状态3："+result);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void sendTask(){
        try {
            sendPack(task.getPack());
        }catch (Exception e){
            Log.e("YUAN","sendTask:",e);
            sendFail("发送数据失败："+e.getMessage());
        }
    }

    /**
     * 发送服务器打包好的数据包
     */
    public void sendPack(List<String> pack){
        writePackAsync(pack);
    }


    private void writePackAsync(List<String> pack){
        if(mBluetoothAdapter==null) {
            throw new BleAdapterUninitializedException();
        }
        if(connectGatt==null) {
            throw new BleNotConnectedException();
        }
        //超时检查
        timeoutHandler.start();
        new Thread(()->{
            try {
                writePack(pack);
            }catch (Exception e){
                sendFail("写入数据包失败："+e.getMessage());
            }
        }).start();
    }

    /**
     * 写入分割好的数据包，可在主线程中调用
     * @param hexArray
     */
    private void writePack(List<String> hexArray){

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i=0;i<hexArray.size();i++){

            byte[] buffer= StringUtils.hexStringToBytes(hexArray.get(i));
            boolean result=  writeBlock(buffer);
            if(!result){
                throw new BleWriteException(String.format("数据包【%d】写入失败",(i+1)));
            }
            try {
                Thread.sleep(PACE);//12ms  17 25
            } catch (InterruptedException e) {
            }
            if(i%5==0){
                try {
                    Thread.sleep(3);//12ms  17 25
                } catch (InterruptedException e) {
                }
            }
            //Log.e("YUAN",String.format("数据包【%d】写入数据结果：%s",(i+1),result+""));
        }
    }

    private boolean writeBlock(byte[] buffer){
        if(connectGatt==null)
            return false;
        BluetoothGattService RxService =connectGatt.getService(RX_SERVICE_UUID);
        if(RxService==null)
            return false;
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if(RxChar==null)
            return false;
        RxChar.setValue(buffer);
        @SuppressLint("MissingPermission") boolean result= connectGatt.writeCharacteristic(RxChar);

        return result;
    }
    //endregion

    private void sendSuccess(BleDeviceFeedback deviceFeedback, BleTask task){
        timeoutHandler.cancel();
        closeConnect();
        if(onSendingListener!=null)
            onSendingListener.onSendSuccessfully(task,deviceFeedback);
    }

    public SendWork setOnSendingListener(OnSendingListener onSendingListener) {
        this.onSendingListener = onSendingListener;
        return this;
    }

    private void sendFail(String message){
        Log.e("YUAN","发送失败："+task.getMac()+" "+message);
        closeConnect();
        if(onSendingListener!=null)
            onSendingListener.onSendFail(task,message);
        onSendingListener=null;
    }

    @SuppressLint("MissingPermission")
    private void closeConnect(){
        if(connectGatt!=null)
            connectGatt.close();
        connectGatt=null;
        if(timeoutHandler!=null)
            timeoutHandler.cancel();
        timeoutHandler=null;
        if(connectTimeoutHandler!=null)
            connectTimeoutHandler.cancel();
        connectTimeoutHandler=null;
    }

    public interface OnSendingListener{
        void onSendSuccessfully(BleTask task, BleDeviceFeedback deviceFeedback);
        void onSendFail(BleTask task,String errorMessage);
    }
}
