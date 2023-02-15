package com.elabel.api.blesdk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.UUID;

@SuppressLint("MissingPermission")
public abstract class TagBleConnectCallback extends BluetoothGattCallback {
    private boolean isConnect;
    private final UUID TX_CHAR_UUID;
    public TagBleConnectCallback(UUID readId){
        TX_CHAR_UUID=readId;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if(newState== BluetoothProfile.STATE_CONNECTED){
            isConnect=true;
            //Log.e("YUAN","onConnectionStateChange==STATE_CONNECTED");
            gatt.discoverServices();
        }else if(newState== BluetoothProfile.STATE_DISCONNECTED){
            //如果以连接成功则，当标签主动断开时不回调通知
            //在写数据时已经处理过，防止重复回调
            if(isConnect)
                return;
            onConnectFail();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if(status==BluetoothGatt.GATT_SUCCESS){
            onConnectSuccess(gatt);
        }else{
            Log.e("YUAN","onServicesDiscovered");
            Log.e("YUAN","连接失败！");
            gatt.close();
            gatt=null;
            onConnectFail();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if(status==BluetoothGatt.GATT_SUCCESS){
            byte[] value = characteristic.getValue();
            if(TX_CHAR_UUID.equals(characteristic.getUuid())) {
                receiveMessage(gatt,value);
            }
        }
    }

    /**
     *  notification 特性的结果
     * @param gatt
     * @param characteristic
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        byte[] value= characteristic.getValue();
        receiveMessage(gatt,value);
    }

    private void receiveMessage(BluetoothGatt gatt,byte[] buffer){
        if(!BleProtocol.encrypt) {
            BleDeviceFeedback deviceFeedback = BleProtocol.getBleFeedback(buffer);
            onReceiveMessage(gatt,deviceFeedback);
        }else {

            String hexString= BleProtocol.decryptReceive(gatt.getDevice().getAddress(),buffer);
            BleDeviceFeedback deviceFeedback= BleProtocol.getBleFeedback(hexString);
            onReceiveMessage(gatt, deviceFeedback);
        }
    }

    protected abstract void onConnectSuccess(BluetoothGatt gatt);
    protected abstract void onConnectFail();
    protected abstract void onReceiveMessage(BluetoothGatt gatt,BleDeviceFeedback deviceFeedback);

}

