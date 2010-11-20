package dfs.server.ds;

import helper.RemoteUtils;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.Properties;

import ligador.LigadorImpl;
import ligador.Service;

import org.junit.*;

import static junit.framework.Assert.*;

//import dfs.exception.BadPositionException;
import dfs.exception.DirectoryServiceException;
//import dfs.exception.InvalidDirectoryEntryException;
//import dfs.exception.NameDuplicateException;
import dfs.server.ds.DirectoryServiceImpl;
import dfs.server.fs.FileService;
import dfs.server.fs.FileServiceImpl;
import dfs.server.fs.FileAttributes.FileType;
import dfs.AccessControl;
import dfs.TestaTudo;
import dfs.Ufid;

public class DirectoryServiceImplTest {
	private static String host;
    private static DirectoryService ds;
    private static FileService  fs;
    private static byte[] rootDir;
    private static String rootDirStr;
    
    public static void start(){
        try {
            fs = FileServiceImpl.getInstance();
            ((FileServiceImpl)fs).setRootPath(TestaTudo.TEST_PATH);
            
            ds = DirectoryServiceImpl.getInstance(fs);
            ((DirectoryServiceImpl) ds).setRootPath(TestaTudo.TEST_PATH);
            
            rootDir = ds.lookup(null, null, 0);
            rootDirStr = new Ufid(rootDir).toString();
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
        	FileService fserv = FileServiceImpl.getInstance();
            ((FileServiceImpl)fserv).setRootPath(TestaTudo.TEST_PATH);
            ((FileServiceImpl)fserv).start(host);
            
            //Gets File Service from Ligador
            Service s = (Service)l.getService(RemoteUtils.FILE_SERVICE);
            fs = (FileService)RemoteUtils.getRemoteObject(s.getHost(), s.getPort(), RemoteUtils.FILE_SERVICE);
            
            
            ds = DirectoryServiceImpl.getInstance();
            ((DirectoryServiceImpl)ds).setRootPath(TestaTudo.TEST_PATH);
            ((DirectoryServiceImpl)ds).start(host);
            
                       
            rootDir = ds.lookup(null, null, 0);
            rootDirStr = new Ufid(rootDir).toString();
            
        } catch (Exception e) {
            fail("Should not throw Exception");
        }
    }
    
    @Test
    public void addName() {
        try {
            Properties p = ((DirectoryServiceImpl) ds).loadProperties(rootDir);
            assertNull(p.getProperty(".."));
            assertEquals(0, (int)fs.getAttributes(rootDir).getReferenceCount());
            
            // Creates and adds test.txt file to root directory
            byte[] ufid2B = fs.create();
            Ufid ufid2 = new Ufid(ufid2B);
            ds.addName(rootDir, "test.txt", ufid2B, 0);
            p = ((DirectoryServiceImpl)ds).loadProperties(rootDir);
            assertEquals(ufid2.toString(), p.getProperty("test.txt"));
            assertEquals(0, (int)fs.getAttributes(rootDir).getReferenceCount());
            ufid2B = ds.lookup(rootDir, "test.txt", 0);
            assertEquals(1, (int)fs.getAttributes(ufid2B).getReferenceCount());
            
            // Creates and adds dev directory to root directory
            byte[] ufid3B = fs.create(FileType.DIRECTORY);
            Ufid ufid3 = new Ufid(ufid3B);
            ds.addName(rootDir, "dev", ufid3B, 0);
            ufid3B = ds.lookup(rootDir, "dev", 0);
            ds.addName(ufid3B, "..", rootDir, 0);
            
            p = ((DirectoryServiceImpl)ds).loadProperties(rootDir);
            assertEquals(ufid3.toString(), p.getProperty("dev"));
            assertEquals(1, (int)fs.getAttributes(rootDir).getReferenceCount());
            
            ufid3B = ds.lookup(rootDir, "dev", 0);
            p = ((DirectoryServiceImpl) ds).loadProperties(ufid3B);
            assertEquals(rootDirStr, p.getProperty(".."));
            assertEquals(1, (int)fs.getAttributes(ufid3B).getReferenceCount());
            
            // Creates and adds test.txt file to dev directory
            byte[] ufid4B = fs.create();
            Ufid ufid4 = new Ufid(ufid4B);
            ufid3B = ds.lookup(rootDir, "dev", 0);
            ds.addName(ufid3B, "test.txt", ufid4B, 0);
            p = ((DirectoryServiceImpl) ds).loadProperties(ufid3B);
            assertEquals(ufid4.toString(), p.getProperty("test.txt"));
            assertEquals(1, (int)fs.getAttributes(ufid3B).getReferenceCount());
            ufid4B = ds.lookup(ufid3B, "test.txt", 0);
            assertEquals(1, (int)fs.getAttributes(ufid4B).getReferenceCount());
            
            this.unName(ufid3B, "test.txt", 1, 0);
            assertEquals(0, (int)fs.getAttributes(ufid4B).getReferenceCount());
            this.unName(rootDir, "dev", 0, 0);
            assertEquals(0, (int)fs.getAttributes(ufid3B).getReferenceCount());
            this.unName(rootDir, "test.txt", 0, 0);
            assertEquals(0, (int)fs.getAttributes(ufid2B).getReferenceCount());
            
            fs.delete(ufid2B);
            fs.delete(ufid3B);
            fs.delete(ufid4B);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    private void unName(byte[] ufid, String fName, int refCount, int uid){
    	try {
    		ds.unName(ufid, fName, uid);
    		Properties p = ((DirectoryServiceImpl) ds).loadProperties(ufid);
    		assertNull(p.getProperty(fName));
    		assertEquals(refCount, (int)fs.getAttributes(ufid).getReferenceCount());
		} catch (DirectoryServiceException e) {
			if( !(e.getCause() instanceof FileNotFoundException))
	    		fail("Should have thrown FileNotFoundException.");
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    	
    }    
    
    @Test
    public void addNameDuplicated() {
        try {
        	byte[] ufid2B = fs.create();
        	byte[] ufid3B = fs.create();
        	Ufid ufid2 = new Ufid(ufid2B);
        	ufid2.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
        	ufid2B = ufid2.encrypt();
        	Ufid ufid3 = new Ufid(ufid3B);
        	ufid3.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
        	ufid3B = ufid3.encrypt();
            try {
            	ds.addName(rootDir, "test.txt", ufid2B, 0);
            	ds.addName(rootDir, "test.txt", ufid3B, 0);
            	
            	fail("Should have thrown DirectoryServiceException.");
            } catch (DirectoryServiceException e) {}
            
            ds.unName(rootDir, "test.txt", 0);
            fs.delete(ufid2B);
            fs.delete(ufid3B);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }

    @Test
    public void addNameNotDir() {
        try{
            byte[] ufid2B = fs.create();
            byte[] ufid3B = fs.create();
            Ufid ufid2 = new Ufid(ufid2B);
            ufid2.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufid2B = ufid2.encrypt();
            Ufid ufid3 = new Ufid(ufid3B);
            ufid3.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufid3B = ufid3.encrypt();
        	try {
        		ds.addName(ufid2B, "directory", ufid3B, 0);
            	fail("Should have thrown DirectoryServiceException.");
        	} catch (DirectoryServiceException e) {
        	}
            fs.delete(ufid2B);
            fs.delete(ufid3B);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @Test
    public void addNameInvalidDirectoryEntry() {
        try{
            byte[] ufid2B = fs.create();
            Ufid ufid2 = new Ufid(ufid2B);
            ufid2.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufid2B = ufid2.encrypt();
        	try {
        		ds.addName(ufid2B, "directory", ufid2B, 0);
        		fail("Should have thrown DirectoryServiceException.");
    		} catch (DirectoryServiceException e) {
    		}
            fs.delete(ufid2B);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @Test
    public void unNameFileNotFound(){
        try{
            byte[] ufidDirB = fs.create(FileType.DIRECTORY);
            Ufid ufidDir = new Ufid(ufidDirB);
            ufidDir.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufidDirB = ufidDir.encrypt();
        	try {
        		ds.unName(ufidDirB, "fileName", 0);
        		fail("Should have thrown DirectoryServiceException.");
    		} catch (DirectoryServiceException e) {
    		}
    		fs.delete(ufidDirB);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @Test
    public void lookup(){
        try{
            byte[] ufid2 = fs.create();
            ds.addName(rootDir, "test.txt", ufid2, 0);
            byte[] ufid3 = ds.lookup(rootDir, "test.txt", 0);
        	try {
    			assertFalse(new Ufid(ufid2).equals(new Ufid(ufid3)));
    			assertEquals(0, (int)fs.getAttributes(rootDir).getOwner());
    			assertEquals(0, (int)fs.getAttributes(ufid3).getOwner());
    		} catch (Exception e) {
    			fail("Should not have thrown " + e.getClass().getName());
    		}
    
    		ds.unName(rootDir, "test.txt", 0);
    		fs.delete(ufid3);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }

    @Test
    public void lookupNotFound(){
    	try {
    		ds.lookup(rootDir, "fileName", 0);
    		fail("Should have thrown DirectoryServiceException.");
		} catch (DirectoryServiceException e) {
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @Test
    public void getNames(){
        try{
        	assertEquals("[]", ds.getNames(rootDir, "*").toString());
        	
        	byte[] ufid2B = fs.create();
        	Ufid ufid2 = new Ufid(ufid2B);
            ufid2.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufid2B = ufid2.encrypt();
        	byte[] ufid3B = fs.create();
        	Ufid ufid3 = new Ufid(ufid3B);
            ufid3.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufid3B = ufid3.encrypt();
        	byte[] ufid4B = fs.create();
        	Ufid ufid4 = new Ufid(ufid4B);
            ufid4.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufid4B = ufid4.encrypt();
        	byte[] ufid5B = fs.create(FileType.DIRECTORY);
        	Ufid ufid5 = new Ufid(ufid5B);
            ufid5.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
            ufid5B = ufid5.encrypt();
        	
        	ds.addName(rootDir, "amarelo", ufid2B, 0);
        	ds.addName(rootDir, "vermelho.txt", ufid3B, 0);
        	ds.addName(rootDir, "azul.txt", ufid4B, 0);
        	ds.addName(rootDir, "laranja", ufid5B, 0);
        	assertEquals("[amarelo, vermelho.txt]", ds.getNames(rootDir, "*el*").toString());
            assertEquals("[azul.txt, vermelho.txt]", ds.getNames(rootDir, "*.txt").toString());
            assertEquals("[amarelo, azul.txt]", ds.getNames(rootDir, "a*").toString());
            assertEquals("[amarelo, azul.txt, laranja]", ds.getNames(rootDir, "*a*").toString());
            assertEquals(4, ds.getNames(rootDir, "*").size());
            assertEquals("[]", ds.getNames(rootDir, "*1txt").toString());
        	
        	ds.unName(rootDir, "amarelo", 0);
        	ds.unName(rootDir, "vermelho.txt", 0);
        	ds.unName(rootDir, "azul.txt", 0);
        	ds.unName(rootDir, "laranja", 0);
            fs.delete(ufid2B);
            fs.delete(ufid3B);
            fs.delete(ufid4B);
            fs.delete(ufid5B);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @AfterClass
    public static void after() {
        try{
            fs.delete(rootDir);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
}
