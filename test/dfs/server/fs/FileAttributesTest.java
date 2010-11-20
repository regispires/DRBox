package dfs.server.fs;

import java.io.File;

import org.junit.*;
import static junit.framework.Assert.*;

import dfs.TestaTudo;
import dfs.Ufid;
import dfs.server.fs.FileAttributes;

public class FileAttributesTest {
    private Ufid ufid;
    private FileAttributes fa;
    private FileAttributes fa2;
    
    @Before
    public void start(){
        this.ufid = new Ufid();
        this.fa = new FileAttributes(ufid.toString());
        this.fa2 = new FileAttributes(ufid.toString());
    }
    
    @Test
    public void storeAndLoad(){
        this.fa.setFileLength(100L);
        this.fa.setOwner(1);
        this.fa.setOwnerPerm((byte)31);
        this.fa.setGroupPerm((byte)0);
        this.fa.setOtherPerm((byte)16);
        
        this.fa.store(TestaTudo.TEST_PATH);
        this.fa2.load(TestaTudo.TEST_PATH);
        assertEquals(100L, (long)this.fa.getFileLength());
        assertEquals(31, (byte)this.fa.getOwnerPerm());
        assertEquals(0, (byte)this.fa.getGroupPerm());
        assertEquals(16, (byte)this.fa.getOtherPerm());
    }
    
    @After
    public void end() {
        File f = new File(TestaTudo.TEST_PATH+"."+ufid);
        f.delete();
    }
}
