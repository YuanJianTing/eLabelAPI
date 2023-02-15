package com.elabel.api.blesdk.exception;

public class BleNotConnectedException extends RuntimeException {

    public BleNotConnectedException() {
        throw new RuntimeException("未连接蓝牙，请些调用 connect方法");
    }

    public BleNotConnectedException(String s) {
        throw new RuntimeException(s);
    }

    public BleNotConnectedException(String message, Throwable cause) {
        throw new RuntimeException(message,cause);
    }

    public BleNotConnectedException(Throwable cause) {
        throw new RuntimeException(cause);
    }
}
