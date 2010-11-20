package helper;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.logging.Level;

import ligador.Ligador;
import ligador.Service;


public class RemoteUtils {
    public static final String LIGADOR = "Ligador";
    public static final String FILE_SERVICE = "FileService";
    public static final String DIR_SERVICE = "DirectoryService";
    
    public static Remote getRemoteObject(String host, int port, String service) throws Exception{
        Remote obj = null;
        System.out.println("Lookup: " + service + " - " + host + ":" + port);
        obj = LocateRegistry.getRegistry(host, port).lookup(service);
        return obj;
    }

    public static byte[] netAddressToByteArray(String address) {
    	byte[] b = new byte[4];
    	String[] addr = address.split("\\.");
    	for (int i=0; i < b.length; i++) {
    		b[i] = (byte)Integer.parseInt(addr[i]);
    	}
    	return b;
    }

    public static String getHostAddress() {
    	String s = null;
    	try {
    		if (System.getProperty("java.rmi.server.hostname") != null) {
    			s = System.getProperty("java.rmi.server.hostname");
    		} else {
    			s = Inet4Address.getLocalHost().getHostAddress();
    		}
    	} catch (UnknownHostException e) {
            Utils.log.log(Level.WARNING,e.getMessage(),e); 
    	}
    	return s;
    }
    
    public static void ligadorRegistry(String host, int port, String hostLigador, int portLigador, String service) {
        try {
            // Registry on Ligador
            Ligador l = (Ligador)RemoteUtils.getRemoteObject(hostLigador, portLigador, RemoteUtils.LIGADOR);
            l.registry(service, new Service(host, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void localRegistry(int port, Remote obj, String service) {
        try {
            // Registry in local RMI Registry
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(port);
            } catch (ExportException e) {
                registry = LocateRegistry.getRegistry();
            }
            registry.rebind(service, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
