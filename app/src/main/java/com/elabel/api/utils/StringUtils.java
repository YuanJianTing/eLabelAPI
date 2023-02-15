package com.elabel.api.utils;

public class StringUtils {
    /**
     * byte数组转化为string
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        //StringBuffer sb=new StringBuffer();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            //sb.append(bytes[j]+",");
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 字符串转化成为16进制字符串
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /// <summary>
    /// Get Temperature Value
    /// </summary>
    /// <param name="temperature">Temperature Data</param>
    /// <returns>Temperature Value</returns>
    public static int getTemperature(int temp) {
        if (temp > 0x7F) temp = (0xFF - temp + 1);
        return temp;
    }

    public  static  int  binaryToInt(String binary) {
        if  (binary ==  null ) {
            System.  out .println( "can't input null ！"  );
        }
        if  (binary.isEmpty()) {
            System.  out .println( "you input is Empty !"  );
        }
        int  max = binary.length();
        String new_binary =  "" ;
        if  (max >= 2 && binary.startsWith( "0" )) {
            int  position = 0;
            for  ( int  i = 0; i < binary.length(); i++) {
                char  a = binary.charAt(i);
                if  (a !=  '0'  ) {
                    position = i;
                    break ;
                }
            }
            if  (position == 0) {
                new_binary = binary.substring(max - 1, max);
            }  else  {
                new_binary = binary.substring(position, max);
            }
        }  else  {
            new_binary = binary;
        }
        int  new_width = new_binary.length();

        long  result = 0;
        if  (new_width < 32) {
            for  ( int  i = new_width; i > 0; i--) {
                char  c = new_binary.charAt(i - 1);
                int  algorism = c -  '0'  ;
                result += Math. pow(2, new_width - i) * algorism;
            }
        }  else  if  (new_width == 32) {
            for  ( int  i = new_width; i > 1; i--) {
                char  c = new_binary.charAt(i - 1);
                int  algorism = c -  '0'  ;
                result += Math. pow(2, new_width - i) * algorism;
            }
            result += -2147483648;
        }
        int  a =  new  Long(result).intValue();
        return  a;
    }
}
