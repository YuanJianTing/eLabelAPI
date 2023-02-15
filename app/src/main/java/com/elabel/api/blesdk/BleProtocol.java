package com.elabel.api.blesdk;

import android.text.TextUtils;

import com.elabel.api.utils.StringUtils;

public class BleProtocol {
    public static final boolean encrypt=true;

    public static String decryptReceive(String mac, byte[] buffer){
        int key_data = 0x00;
        int key_data_key = 'b';
        byte MAC_XOR = 0;
        byte[] mac_hex = StringUtils.hexStringToBytes(mac.replace(":", ""));
        int k;
        for (k = 0; k < 6; k++)//利用MAC地址进行异或运算
            MAC_XOR = (byte)(MAC_XOR ^ mac_hex[k]);
        for(k=0;k<20;k++)//calcute miyao date
        {
//            if(k == 17)
//                key_next =(byte) buffer[17];
            buffer[k] = (byte)(buffer[k]^MAC_XOR);
            buffer[k] = (byte)(buffer[k]^key_data_key);
        }
        return StringUtils.bytesToHex(buffer);
    }

    public static  BleDeviceFeedback getBleFeedback(String hexString){
        BleDeviceFeedback feedback=new BleDeviceFeedback();
        if(TextUtils.isEmpty(hexString))
            return feedback;
        byte[] buffer= StringUtils.hexStringToBytes(hexString);
        return getBleFeedback(buffer);
    }
    public static  BleDeviceFeedback getBleFeedback(byte[] buffer){
        BleDeviceFeedback feedback=new BleDeviceFeedback();
        if(buffer==null||buffer.length==0)
            return feedback;
        if(buffer.length>2) {
            /**
             * 3字节电量
             * 4字节温度
             * */
            int power = buffer[2];//电量
            int temperature = StringUtils.getTemperature(buffer[3]);//温度

            feedback.setPower(power / 10.0);
            feedback.setTemperature(temperature);
            return feedback;
        }
        return feedback;
    }
}
