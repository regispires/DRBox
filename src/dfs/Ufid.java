package dfs;
import helper.ByteUtils;
import helper.RemoteUtils;
import helper.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import dfs.server.fs.FileServiceImpl;

public class Ufid implements Serializable {
	private static final long serialVersionUID = 2792733029856676753L;
	
	private byte[] inetAddress;
    private Date date;
    private int fileNumber;
    private int randNumber;
    private byte permission;
    private static Logger log = Logger.getLogger(Ufid.class.getName());
    private static final byte[] KEY = "chave de 16bytes".getBytes();
    public static final int UFID_DEFAULT_LENGTH = 20;
    
    public Ufid() {
        this.inetAddress = RemoteUtils.netAddressToByteArray(RemoteUtils.getHostAddress());
        this.date = new Date();
        try {
			this.fileNumber = FileServiceImpl.getInstance().getFileNumber();
		} catch (RemoteException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
        this.randNumber = (int)(Math.random() * 4294967296l);
    }
    
    /**
     * 
     * @param s
     * @param rootDir
     */
    public Ufid(String s) {
		// inetAddr date             fileNumb randNumber
		// 7f000101|0000012c0d26379d|00000006|7fffffff
		// 0        4                12       16
		byte[] b = ByteUtils.hexStringToByteArray(s);
		this.inetAddress = Arrays.copyOf(b, 4);
		this.date = new Date(ByteUtils.byteArrayToLong(b,4));
		this.fileNumber = ByteUtils.byteArrayToInt(b,12);
		this.randNumber = ByteUtils.byteArrayToInt(b,16);
    }
    
    public Ufid(byte[] b) {
    	this.inetAddress = Arrays.copyOfRange(b, 0, 4);
    	this.date = new Date(ByteUtils.byteArrayToLong(Arrays.copyOfRange(b, 4, 12)));
    	this.fileNumber = ByteUtils.byteArrayToInt(Arrays.copyOfRange(b, 12, 16));
    	if(b.length == UFID_DEFAULT_LENGTH){
    		this.randNumber = ByteUtils.byteArrayToInt(Arrays.copyOfRange(b, 16, 20));
    	}else{
    		byte[] decrypted = Utils.decrypt(Arrays.copyOfRange(b, 16, 32), KEY);
    		this.randNumber = ByteUtils.byteArrayToInt(Arrays.copyOfRange(decrypted, 0, 4));
    		this.permission = decrypted[4];
    	}
    }
    
    
    @Override
    public String toString() {
        String inetAddress = ByteUtils.bytesToString(this.inetAddress); 
        String date = ByteUtils.bytesToString(ByteUtils.longToByteArray(this.date.getTime()));
        String fileNumber = ByteUtils.bytesToString(ByteUtils.intToByteArray(this.fileNumber));
        String randNumber = ByteUtils.bytesToString(ByteUtils.intToByteArray(this.randNumber));
        log.fine("inetAddress: " + this.inetAddress + " " + inetAddress);
        log.fine("date: " + this.date + " " + date);
        log.fine("fileNumber: " + this.fileNumber + " " + fileNumber);
        log.fine("randNumber: " + this.randNumber + " " + randNumber);
        return inetAddress + date + fileNumber + randNumber;
    }
    
    public byte[] getBytes(){
    	ByteBuffer bb = ByteBuffer.allocate(20);
		bb.put(this.inetAddress);
		bb.put(ByteUtils.longToByteArray(this.date.getTime()));
		bb.put(ByteUtils.intToByteArray(this.fileNumber));
		bb.put(ByteUtils.intToByteArray(this.randNumber));
        return bb.array();
    }
    
    public byte[] encrypt() {
    	ByteArrayBuffer bab = new ByteArrayBuffer();
		byte[] rn = ByteUtils.intToByteArray(this.randNumber);
        byte[] message = {rn[0], rn[1], rn[2], rn[3], this.permission};
    	try {
			bab.write(this.inetAddress);
			bab.write(ByteUtils.longToByteArray(this.date.getTime()));
			bab.write(ByteUtils.intToByteArray(this.fileNumber));
			bab.write(Utils.crypt(message, KEY));
			bab.write(this.permission);
		} catch (IOException e) {
            log.log(Level.WARNING,e.getMessage(),e); 
		}
        return bab.getRawData();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ufid))
            return false;
        Ufid ufid = (Ufid) obj;
        if (Arrays.equals(this.inetAddress, ufid.inetAddress) &&  
                this.date.equals(ufid.date) && 
                this.fileNumber == ufid.fileNumber &&
                this.randNumber == ufid.randNumber &&
                this.permission == ufid.permission)
            return true;
        
        return false;
    }
    
    public byte[] getInetAddress() {
        return inetAddress;
    }
    
    public void setInetAddress(byte[] inetAddress) {
        this.inetAddress = inetAddress;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public int getFileNumber() {
        return fileNumber;
    }
    
    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }
    
    public int getRandNumber() {
        return randNumber;
    }
    
    public void setRandNumber(int randNumber) {
        this.randNumber = randNumber;
    }

	public byte getPermission() {
		return permission;
	}

	public void setPermission(byte permission) {
		this.permission = permission;
	}
    
}
