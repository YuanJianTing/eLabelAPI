package com.elabel.api.blesdk;

import android.bluetooth.BluetoothDevice;

public interface OnScanBleListener {
    void onAddBluetoothDevice(BluetoothDevice device, int rssi);
}
