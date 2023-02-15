package com.elabel.api.blesdk.exception;

public class BleWriteException extends RuntimeException {

    public BleWriteException() {
        throw new RuntimeException("未连接蓝牙，请些调用 connect方法");
    }

    public BleWriteException(String s) {
        throw new RuntimeException(s);
    }

    public BleWriteException(String message, Throwable cause) {
        throw new RuntimeException(message,cause);
    }

    public BleWriteException(Throwable cause) {
        throw new RuntimeException(cause);
    }
}
