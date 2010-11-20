package dfs.client;

import helper.RemoteUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.util.Arrays;

import ligador.LigadorImpl;

import org.junit.*;

import dfs.TestaTudo;
import dfs.exception.OperationNotCompletedException;
import dfs.exception.PermissionDeniedException;
import dfs.server.ds.DirectoryService;
import dfs.server.ds.DirectoryServiceImpl;
import dfs.server.fs.FileService;
import dfs.server.fs.FileServiceImpl;
import dfs.server.fs.FileAttributes.FileType;
import static junit.framework.Assert.*;

public class ClientModuleImplTest {
    private static ClientModule cm;
    private static String host;
    
    
    public static void start(){
        try{
        	FileService fs = FileServiceImpl.getInstance();
            ((FileServiceImpl)fs).setRootPath(TestaTudo.TEST_PATH);
            
            DirectoryService ds = DirectoryServiceImpl.getInstance(fs);
            ((DirectoryServiceImpl)ds).setRootPath(TestaTudo.TEST_PATH);
            
            cm = ClientModuleImpl.getInstance();
            ClientModuleImpl.fs = fs;
            ClientModuleImpl.ds = ds;
            
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
        	FileService fs = FileServiceImpl.getInstance();
            ((FileServiceImpl)fs).setRootPath(TestaTudo.TEST_PATH);
            ((FileServiceImpl)fs).start(host);
            
            DirectoryService ds = DirectoryServiceImpl.getInstance();
            ((DirectoryServiceImpl)ds).setRootPath(TestaTudo.TEST_PATH);
            ((DirectoryServiceImpl)ds).start(host);
            
            cm = ClientModuleImpl.getInstance();
            ((ClientModuleImpl)cm).start(RemoteUtils.getHostAddress());
            
        } catch (Exception e) {
            fail("Should not throw Exception");
        }
    }
    
    @Test
    public void createPermissionDenied() {
        try {
            cm.create("/regis", FileType.DIRECTORY, 1);
            fail("Should throw PermissionDeniedException.");
        } catch (PermissionDeniedException e) {
            
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }

    @Test
    public void create() {
        try {
            cm.create("/regis", FileType.DIRECTORY, 0);
            cm.create("/diego", FileType.DIRECTORY, 0);
            cm.create("/diego/text.txt", FileType.FILE, 0);
            cm.rm("/regis", 0);
            cm.rm("/diego/text.txt", 0);
            cm.rm("/diego", 0);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    @Test
    public void createOperationNotPermitted() {
        try {
            try {
                cm.create("/diego", FileType.DIRECTORY, 0);
                cm.create("/diego/text.txt", FileType.FILE, 0);
                cm.rm("/diego", 0);
                cm.rm("/diego/text.txt", 0);
                fail("Should throw OperationNotPermittedException");
            } catch (OperationNotCompletedException e) {
            }
            cm.rm("/diego/text.txt", 0);
            cm.rm("/diego", 0);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }

    @Test
    public void upload() {
        File f = new File("test.txt");
        FileOutputStream out = null;
        FileInputStream in = null;
        try {
            byte[] b1 = "Distributed File System".getBytes();
            out = new FileOutputStream(f);
            out.write(b1);
            cm.upload("test.txt", "/", 0);
            f.delete();
            cm.download(".", "/test.txt", 0);
            byte[] b2 = new byte[(int)f.length()];
            in = new FileInputStream(f);
            in.read(b2);
            f.delete();
            assertTrue(Arrays.equals(b1, b2));
            cm.rm("/test.txt", 0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void moveAndLs() {
        try {
            cm.create("/regis", FileType.DIRECTORY, 0);
            cm.create("/diego", FileType.DIRECTORY, 0);
            cm.create("/diego/test.txt", FileType.FILE, 0);
            cm.create("/diego/aaa", FileType.DIRECTORY, 0);
            cm.move("/diego/test.txt", "/regis", 0);
            cm.move("/diego/aaa", "/regis", 0);
    
            assertEquals("[.., aaa, test.txt]", cm.ls("/regis", "*", 0).toString());
            assertEquals("[..]", cm.ls("/diego", "*", 0).toString());
            
            cm.rm("/regis/test.txt", 0);
            cm.rm("/regis/aaa", 0);
            cm.rm("/regis", 0);
            cm.rm("/diego", 0);
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
    
    @Test
    public void removeRootDir() {
        try {
            cm.rm("/", 0);
            fail("Should throw OperationNotCompletedException");
        } catch (OperationNotCompletedException e) {
        } catch (RemoteException e) {
            fail("Should not throw RemoteException");
        }
    }
    
}
