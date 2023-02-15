package com.elabel.api.blesdk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TagScanCallback extends ScanCallback {
    private onScanResult onScanResult;

    public void setOnScanResult(TagScanCallback.onScanResult onScanResult) {
        this.onScanResult = onScanResult;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        if(result.getDevice()==null|| TextUtils.isEmpty(result.getDevice().getName()))
            return;
        if(onScanResult!=null)
            onScanResult.onScanResult(result.getDevice(),result.getRssi());
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
    }

    public interface onScanResult{
        void onScanResult(BluetoothDevice device, int rssi);
    }
}
