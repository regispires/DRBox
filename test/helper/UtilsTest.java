package helper;

import java.util.Arrays;
import java.util.Properties;

import org.junit.*;
import static junit.framework.Assert.*;

public class UtilsTest {

	@Test
	public void byteArrayToProperties() {
		Properties p1 = new Properties();
		p1.put("chave1", "valor1");
		p1.put("chave2", "valor2");
		byte[] b = Utils.propertiesToByteArray(p1);
		
		Properties p2 = Utils.byteArrayToProperties(b);
		assertEquals(p1.get("chave1"), p2.get("chave1"));
		assertEquals(p1.get("chave2"), p2.get("chave2"));
	}
	
	public void getHostAddress() {
		System.setProperty("java.rmi.server.hostname", "200.128.201.202");
		byte[] b = {(byte)200, (byte)128, (byte)201, (byte)202};
		assertTrue(Arrays.equals(b, RemoteUtils.netAddressToByteArray(RemoteUtils.getHostAddress())));
	}
	
}
