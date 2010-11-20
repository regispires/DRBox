package dfs.server.ds;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface DirectoryService extends Remote {
	
	
    /**
     * Locates the text name in the directory and returns the relevant UFID.
     * @param ufidDir
     * @param fileName
     * @param uid - user id.
     * @return ufid
     */
    public byte[] lookup(byte[] ufidDir, String fileName, int uid) throws RemoteException;
    
    /**
     * Adds fileName to the directory and updates the file attributes.
     * The root directory has ufidDir = ufid.
     * @param ufidDir
     * @param fileName
     * @param ufid
     * @param uid
     */
    public byte[] addName(byte[] ufidDir, String fileName, byte[] ufid, int uid) throws RemoteException;
    
    /**
     * Removes fileName from directory 
     * @param ufidDir
     * @param fileName
     */
    public byte[] unName(byte[] ufidDir, String fileName, int uid) throws RemoteException;
    
    /**
     * Returns all the text names in the directory that match the pattern
     * expression.
     * @param ufidDir
     * @param pattern
     * @return Name of files that match the pattern. 
     */
    public List<String> getNames(byte[] ufidDir, String pattern) throws RemoteException;

}
