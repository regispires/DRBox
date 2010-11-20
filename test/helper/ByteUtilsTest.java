package helper;

import java.util.Arrays;

import org.junit.*;
import static junit.framework.Assert.*;

import helper.ByteUtils;

public class ByteUtilsTest {
    @Test
    public void hexStringToByteArray() {
        byte[] b = ByteUtils.hexStringToByteArray("7f000101");
        byte[] b2 = {0x7f, 0x00, 0x01, 0x01};
        assertTrue(Arrays.equals(b2, b));
    }
}
