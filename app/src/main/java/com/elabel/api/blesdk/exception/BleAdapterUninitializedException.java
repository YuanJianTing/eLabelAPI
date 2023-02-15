package com.elabel.api.blesdk.exception;

public class BleAdapterUninitializedException extends RuntimeException {


    public BleAdapterUninitializedException() {
        throw new RuntimeException("低功耗蓝牙适配器尚未初始化");
    }

    public BleAdapterUninitializedException(String s) {
        throw new RuntimeException(s);
    }

    public BleAdapterUninitializedException(String message, Throwable cause) {
        throw new RuntimeException(message,cause);
    }

    public BleAdapterUninitializedException(Throwable cause) {
        throw new RuntimeException(cause);
    }

}