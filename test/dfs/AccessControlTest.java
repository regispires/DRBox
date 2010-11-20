package dfs;
import org.junit.*;

import static junit.framework.Assert.*;

public class AccessControlTest {
    @Test
    public void checkReadPermission(){
        Ufid ufid = new Ufid();
        ufid.setPermission((byte)18);
        assertTrue(AccessControl.checkPermission(ufid.getPermission(), 'r'));
    }

}
