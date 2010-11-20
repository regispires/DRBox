package dfs.server.fs;


import helper.RemoteUtils;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Calendar;

import ligador.LigadorImpl;

import org.junit.*;

import static junit.framework.Assert.*;

import dfs.AccessControl;
import dfs.TestaTudo;
import dfs.Ufid;
import dfs.exception.BadPositionException;
import dfs.exception.FileServiceException;
import dfs.server.fs.FileAttributes;
import dfs.server.fs.FileServiceImpl;

public class FileServiceImplTest {
	private static String host;
    private static FileServiceImpl fs;
    private Ufid ufid;
    private byte[] ufidB;
    
    public static void before(){
    	try {
	    	fs = FileServiceImpl.getInstance();
	    	fs.setRootPath(TestaTudo.TEST_PATH);
    	} catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @BeforeClass
    public static void startRmi(){
        try{
        	LigadorImpl l = LigadorImpl.getInstance();
        	l.start();
        	
        	host = RemoteUtils.getHostAddress();
        	fs = FileServiceImpl.getInstance();
            ((FileServiceImpl)fs).setRootPath(TestaTudo.TEST_PATH);
            ((FileServiceImpl)fs).start(host);
            
            
        } catch (Exception e) {
            fail("Should not throw Exception");
        }
    }
    
    @Before
    public void start() {
        this.ufid = new Ufid(fs.create());
        this.ufid.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
        ufidB = ufid.encrypt();
    }
    
    @Test
    public void create() {
		File f = new File(TestaTudo.TEST_PATH+this.ufid.toString());
		assertTrue(f.isFile());
		FileAttributes fa = fs.getAttributes(this.ufidB); 
		assertEquals(0L, (long)fa.getFileLength());
		assertEquals(FileAttributes.FileType.FILE, fa.getFileType());
    }	

    @Test
    public void delete() {
		byte[] ufid2B = fs.create();
		Ufid ufid2 = new Ufid(ufid2B);
      	fs.delete(ufid2B);
      	File f = new File(TestaTudo.TEST_PATH+ufid2.toString());
      	assertFalse(f.isFile());
    }
    
    @Test
    public void writeAndRead(){
        try {
            byte[] data = "Distributed File System".getBytes();
            fs.write(this.ufidB, 0, data);
            byte[] data2 = fs.read(this.ufidB, 0, data.length);
            assertTrue(Arrays.equals(data, data2));
            
            byte[] data3 = fs.read(this.ufidB, 20, 10);
            assertTrue(Arrays.equals("tem".getBytes(), data3));
            
            
            FileAttributes fa = fs.getAttributes(ufidB);
    
            // Checks file length
            File f = new File(TestaTudo.TEST_PATH+this.ufid.toString());
            assertEquals(f.length(), (long)fa.getFileLength());
            
            // Checks write timestamp
            Calendar c = Calendar.getInstance();
            c.setTime(fa.getWriteTimestamp());
            Calendar c2 = Calendar.getInstance();
            assertEquals(c2.get(Calendar.DAY_OF_YEAR), c.get(Calendar.DAY_OF_YEAR));
            
            // Checks read timestamp
            c.setTime(fa.getReadTimestamp());
            assertEquals(c2.get(Calendar.DAY_OF_YEAR), c.get(Calendar.DAY_OF_YEAR));
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    public void truncate() {
        try {
        	 byte[] data = "Distributed File System".getBytes();
             fs.write(this.ufidB, 0, data);
           	 fs.truncate(this.ufidB, 3L);
           	 byte[] data2 = fs.read(this.ufidB, 0, data.length);
           	 assertTrue(Arrays.equals("Dis".getBytes(), data2));
         } catch (RemoteException e) {
             fail("Should not throw RemoteException");
         }
    }
    
    @Test
    public void writeIdempotent(){
        try {
        	 byte[] data = "Distributed File System".getBytes();
             fs.write(this.ufidB, 0, data);
             fs.write(this.ufidB, 12, "FILE".getBytes());
             fs.write(this.ufidB, 12, "FILE".getBytes());
             byte[] data2 = fs.read(this.ufidB, 0, data.length);
             assertTrue(Arrays.equals("Distributed FILE System".getBytes(), data2));
             
             // Checks file length
             assertEquals(data.length, (long)fs.getAttributes(ufidB).getFileLength());
         } catch (RemoteException e) {
             fail("Should not throw RemoteException");
         }
         
    }
    
    @Test
    public void writeBadPosition() {
    	try {
    		byte[] data = "Distributed File System".getBytes();
    		fs.write(this.ufidB, 10, data);
		} catch (FileServiceException e) {
			if( !(e.getCause() instanceof BadPositionException))
	    		fail("Should have thrown BadPositionException.");
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @Test
    public void readBadPosition() {
    	try {
    		byte[] data = "Distributed File System".getBytes();
    		fs.write(this.ufidB, 0, data);
    		fs.read(this.ufidB, 10, data.length);
		} catch (FileServiceException e) {
			if( !(e.getCause() instanceof BadPositionException))
	    		fail("Should have thrown BadPositionException.");
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    
    @After
    public void end(){
		fs.delete(this.ufidB);
    }
    
}
