package helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    static Logger log = Logger.getLogger(Utils.class.getName());
	
    public static String propertiesToStr(Properties p) {
        StringBuilder sb = new StringBuilder();
        Set<String> set = p.stringPropertyNames();
        for (String s : set) 
            sb.append(s).append(":").append(p.getProperty(s)).append("\n");
        return sb.toString();
    }
    
    public static Properties byteArrayToProperties(byte[] b){
    	ByteArrayInputStream in = new ByteArrayInputStream(b);
    	Properties p = new Properties();
    	try {
			p.load(in);
		} catch (IOException e) {
            log.log(Level.WARNING,e.getMessage(),e); 
		}
    	return p;
    }
    
    public static  byte[] propertiesToByteArray(Properties p) {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
			p.store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return out.toByteArray();
    }
    
    public static byte[] crypt(byte[] message, byte[] key) {
    	byte[] encrypted = null;
    	try {
    		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
    		encrypted = cipher.doFinal(message);
    	} catch (Exception e) {
            log.log(Level.WARNING,e.getMessage(),e); 
    	}
    	return encrypted;
    }
    
    public static byte[] decrypt(byte[] encrypted, byte[] key) {
    	byte[] decrypted = null;
    	try {
    		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
    		decrypted = cipher.doFinal(encrypted);
    	} catch (Exception e) {
            log.log(Level.WARNING,e.getMessage(),e); 
    	}
    	return decrypted;
    }
    
}
