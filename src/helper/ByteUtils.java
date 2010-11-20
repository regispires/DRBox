package helper;

import java.nio.ByteBuffer;

public class ByteUtils {
    public static byte[] intToByteArray(int n) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(n);
        return bb.array();
    }
        
    public static byte[] longToByteArray(long n) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(n);
        return bb.array();
    }        
        
    public static int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b,0);
    }
    
    public static int byteArrayToInt(byte[] b, int start) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        return bb.getInt(start);
    }
    
    public static long byteArrayToLong(byte[] b) {
        return byteArrayToLong(b, 0);
    }
    
    public static long byteArrayToLong(byte[] b, int start) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        return bb.getLong(start);
    }
    
    public static String bytesToString(byte[] bytes) {
        return bytesToString(bytes, null);
    }
    
    public static String bytesToString(byte[] bytes, String delimiter) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String str = Integer.toHexString(bytes[i] & 0xFF);
            if (str.length() == 1)
                sb.append('0');
            sb.append(str);
            if (delimiter != null)
                sb.append(delimiter);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length()/2];
        for (int i=0; i < s.length()-1; i+=2) {
            String hs = "" + s.charAt(i) + s.charAt(i+1);
            b[i/2] = (byte)Integer.parseInt(hs, 16);
        }
        return b;
    }
}
