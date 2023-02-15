package com.elabel.api.blesdk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BleManager {
    private final String NAME_FILTER = "easyTag";//蓝牙名称过滤
    public static final UUID RX_SERVICE_UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123");//UART Sevice

    private static BleManager instance;

    public static BleManager getInstance(){
        if(instance==null)
            instance=new BleManager();
        return instance;
    }


    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private Map<String, BluetoothDevice> deviceMap;
    private TagScanCallback tagScanCallback;
    private BluetoothLeScanner scanner ;
    //连接对象
    private List<OnScanBleListener> onScanBleListener;
    //是否正在扫描
    private boolean scanBle;
    //是否位主动断开
    private ScanSettings scanSettings;
    //扫描锁定
    private boolean scanLock=false;
    private boolean scanState=false;
    //正在发送的 监听
    private  List<ScanFilter> filters;

    public void init(Context context){
        mContext=context;
        deviceMap=new HashMap<>();
        onScanBleListener=new ArrayList<>();
        BluetoothManager bluetoothManager=(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter==null)
            return;
        filters = new ArrayList<>();
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString(RX_SERVICE_UUID.toString()))
                .build();
        filters.add(scanFilter);
        tagScanCallback = new TagScanCallback();
        tagScanCallback.setOnScanResult(new TagScanCallback.onScanResult() {
            @Override
            public void onScanResult(BluetoothDevice device, int rssi) {
                addBluetoothDevice(device,rssi);
            }
        });
        scanner = mBluetoothAdapter.getBluetoothLeScanner();

        ScanSettings.Builder builder=new ScanSettings.Builder()
                //设置高功耗模式
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //android 6.0添加设置回调类型、匹配模式等
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            //定义回调类型
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
            //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
            builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
        }
        //芯片组支持批处理芯片上的扫描
        if (mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
            //设置蓝牙LE扫描的报告延迟的时间（以毫秒为单位）
            //设置为0以立即通知结果
            builder.setReportDelay(0L);
        }
        scanSettings=builder.build();
    }

    /**
     * 是否支持蓝牙
     * @return
     */
    public boolean isSupportBle(){
        return mBluetoothAdapter!=null;
    }

    /**
     *蓝牙是否开启
     * @return
     */
    public boolean isBlueEnable(){
        if(mBluetoothAdapter==null)
            return false;
        return mBluetoothAdapter.isEnabled();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /**
     * 开启蓝牙
     */
    @SuppressLint("MissingPermission")
    public void enableBluetooth(){
        mBluetoothAdapter.enable();
    }



    private Runnable scanTask=()->{
        while (scanState){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!isBlueEnable()){
                Log.e("ETAG","蓝牙未开启--------》");
                continue;
            }
            scanBle(null);
            try {
                Thread.sleep(1000*20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopScan();
        }
    };

    /**
     * 关闭持续扫描
     */
    public void stopContinuedScan(){
        scanState=false;
    }

    /**
     * 开启持续扫描
     */
    public void runContinuedScan(){
        if(scanState)
            return;
        scanState=true;
        new Thread(scanTask).start();
    }

    /**
     * 扫描蓝牙
     * @param mac 指定要扫描的mac地址；为空则扫描所有
     */
    @SuppressLint("MissingPermission")
    public void  scanBle(String mac){
        if(scanLock)
            return;
        if(scanBle)
            return;
        if(mBluetoothAdapter==null)
            return;
        if(scanner==null)
            return;
        scanBle=true;
        if(!TextUtils.isEmpty(mac)) {
            List<ScanFilter> filters=new ArrayList<>();
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setDeviceAddress(mac)
                    .setServiceUuid(ParcelUuid.fromString(RX_SERVICE_UUID.toString()))
                    .build();
            filters.add(scanFilter);
            scanner.startScan(filters, scanSettings, tagScanCallback);
        }else
            scanner.startScan(this.filters, scanSettings, tagScanCallback);
    }


    /**
     * 停止扫描
     */
    @SuppressLint("MissingPermission")
    public void stopScan(){
        if(!scanBle)
            return;
        if(scanner==null)
            return;
        scanBle=false;
        scanner.stopScan(tagScanCallback);
    }

    /**
     * 添加标签
     * @param device
     * @param rssi
     */
    @SuppressLint("MissingPermission")
    private void addBluetoothDevice(BluetoothDevice device, int rssi) {
        String name=device.getName();
        if(name!=null&& name.startsWith(NAME_FILTER)) {
            String mac = device.getAddress();
            if(deviceMap.containsKey(mac))
                return;
            deviceMap.put(mac, device);
            //Log.e("YUAN","Address="+device.getAddress()+" rssi="+rssi);
            for (OnScanBleListener l:onScanBleListener) {
                l.onAddBluetoothDevice(device,rssi);
            }
        }
    }


    public BluetoothDevice getDeviceByMac(String mac){
        if(!deviceMap.containsKey(mac))
            return null;
        return deviceMap.get(mac);
    }

    public int getCacheCount(){
        return deviceMap.size();
    }


    public void closeConnect(){
        scanLock=false;
    }

    public void addOnScanBleListener(OnScanBleListener onScanBleListener) {
        this.onScanBleListener.add(onScanBleListener);
    }
    public void removeOnScanBleListener(OnScanBleListener onScanBleListener) {
        this.onScanBleListener.remove(onScanBleListener);
    }
}
