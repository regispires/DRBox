package dfs;

import org.junit.*;
import static junit.framework.Assert.*;

import dfs.Ufid;

public class UfidTest {
    @Test
    public void ufid() {
        
        Ufid ufid = new Ufid();
        String s = ufid.toString();
        Ufid ufid2 = new Ufid(s);
        assertEquals(ufid, ufid2);
        assertEquals(ufid, new Ufid(ufid.getBytes()));
    }
    
    @Test 
    public void encrypt() {
    	Ufid ufid = new Ufid();
    	ufid.setPermission((byte)31);
    	byte[] b1 = ufid.encrypt();
    	assertEquals(ufid, new Ufid(b1));
    }
    
}
