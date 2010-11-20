package dfs.server.ds;

import helper.FileUtils;
import helper.RemoteUtils;
import helper.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import ligador.Ligador;
import ligador.Service;
import java.util.logging.Logger;

import dfs.AccessControl;
import dfs.Ufid;
import dfs.exception.DirectoryServiceException;
import dfs.exception.InvalidDirectoryEntryException;
import dfs.exception.NameDuplicateException;
import dfs.exception.PermissionDeniedException;
import dfs.server.fs.FileAttributes;
import dfs.server.fs.FileService;
import dfs.server.fs.FileAttributes.FileType;

public class DirectoryServiceImpl extends UnicastRemoteObject implements DirectoryService {
	private static final long serialVersionUID = -1276324703673573029L;
	private static DirectoryServiceImpl ds;
	private static FileService fs;
	
	private static Logger log = Logger.getLogger(DirectoryServiceImpl.class.getName());
    private static Ufid rootDir;
    private String rootPath;
    
    private DirectoryServiceImpl() throws RemoteException{
    	setRootPath(System.getProperty("user.home")+"/dfs");
    }
    
    public static DirectoryServiceImpl getInstance(FileService fserv) throws RemoteException{
    	fs = fserv;
    	return getInstance();
    }
    
    public static DirectoryServiceImpl getInstance() throws RemoteException{
    	rootDir = null;
    	if(ds == null)
    		ds = new DirectoryServiceImpl();
    	return ds;
    }
    
    private Ufid getRootDir() throws RemoteException{
    	if (rootDir != null)
    		return rootDir;
    	
    	Properties p = null;
    	Ufid ufidRoot = null;
		p = FileUtils.loadProperties(rootPath + "/root_dir");
    	if (p.getProperty("ufid") == null) {
    		ufidRoot = createRootDir();
    	} else {
    		ufidRoot = new Ufid(p.getProperty("ufid"));
    		ufidRoot.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
    		try {
    			fs.read(ufidRoot.encrypt(), 0, 0);
    		} catch (Exception e) {
    			log.warning("rootDir recreated");
    			ufidRoot = createRootDir();
			}
    	}
    	ufidRoot.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
    	p.setProperty("ufid", ufidRoot.toString());
    	FileUtils.storeProperties(rootPath + "/root_dir", p);
    	rootDir = ufidRoot;
        return rootDir;
    }
    
    private Ufid createRootDir() throws RemoteException {
		Ufid ufidRoot = new Ufid(fs.create(FileType.DIRECTORY));
		FileAttributes fa = new FileAttributes();
		fa.setOwnerPerm(AccessControl.DEFAULT_OWNER_PERMISSION);
		fa.setGroupPerm(AccessControl.DEFAULT_GROUP_PERMISSION);
		fa.setOtherPerm(AccessControl.DEFAULT_OTHER_PERMISSION);
		fa.setOwner(0);
		fs.setAttributes(ufidRoot.getBytes(), fa);
		return ufidRoot;
    }
    
    @Override
    public byte[] lookup(byte[] ufidDirB, String fileName, int uid) throws RemoteException{
    	if(ufidDirB == null){
    		Ufid rootDir = getRootDir();
    	    setUfidPermission(rootDir, uid);
    		return rootDir.encrypt();
    	}
    	
    	if(fs.getAttributes(ufidDirB).getFileType() != FileType.DIRECTORY)
    		throw new DirectoryServiceException(new IllegalArgumentException("'ufidDirB' must be a directory"));
    	
    	//Ufid ufid = lookup(ufidDirB, fileName);
    	Properties p = loadProperties(ufidDirB);
    	String ufidStr = p.getProperty(fileName);
    	
    	if(ufidStr == null){
    		throw new DirectoryServiceException(new FileNotFoundException(fileName + " not found."));
    	}
    	Ufid ufid = new Ufid(ufidStr);
    	//getAttributes permission is necessary to generate user UFID
    	ufid.setPermission(AccessControl.getPermission("g"));
    	
    	
    	setUfidPermission(ufid, uid);
    	return ufid.encrypt();
    }
    
    private void setUfidPermission(Ufid ufid, int uid) throws RemoteException {
        FileAttributes fa = fs.getAttributes(ufid.encrypt());
        if (uid == fa.getOwner()) {
            ufid.setPermission(fa.getOwnerPerm() != null ? fa.getOwnerPerm() : 0);
        } else {
            ufid.setPermission(fa.getOtherPerm() != null ? fa.getOtherPerm() : 0);
        }
    }

    @Override
    public byte[] addName(byte[] ufidDirB, String fileName, byte[] ufidB, int uid) throws RemoteException{
        //Decriptes the ufids.
    	Ufid ufidDir = new Ufid(ufidDirB);
    	
    	// Verifies uid permission.
    	if(!AccessControl.checkPermission(ufidDir.getPermission(), 'w'))
    	    throw new PermissionDeniedException("addName");
    	
    	// uid is the owner.
    	Ufid ufid = new Ufid(ufidB);

    	// ufidDir cannot be equal to ufid
    	if (ufidDir.equals(ufid)) {
    	    throw new DirectoryServiceException(new InvalidDirectoryEntryException("ufidDir cannot be equal to ufid"));
    	}
    	
        String ufidDirStr = ufidDir.toString();
        String ufidStr = ufid.toString();
        
        // checks is ufidDir is a directory
        FileAttributes parentFa = fs.getAttributes(ufidDirB);
        if(parentFa.getFileType() != FileType.DIRECTORY){
            throw new DirectoryServiceException(new IllegalArgumentException(ufidDirStr+" is not a directory"));
        }
        Properties p = loadProperties(ufidDirB);
        
        if (p.getProperty(fileName) != null) {
            throw new DirectoryServiceException(new NameDuplicateException(fileName + " already exists"));
        }
        
    	p.setProperty(fileName, ufidStr);
    	storeProperties(ufidDirB, p);

    	ufid.setPermission(AccessControl.DEFAULT_OWNER_PERMISSION);
    	ufidB = ufid.encrypt();
        FileAttributes fa = fs.getAttributes(ufidB);
        fs.setAttributes(ufidB, new FileAttributes(null, fa.getReferenceCount()+1, uid));
        return ufidB;
    }

    @Override
    public byte[] unName(byte[] ufidDirB, String fileName, int uid) throws RemoteException{
    	
        //Gets fileName ufid.
        byte[] ufidB = this.lookup(ufidDirB, fileName, uid);
        Ufid ufid = new Ufid(ufidB);
        
        if(! AccessControl.checkPermission(ufid.getPermission(), 'd'))
        	throw new PermissionDeniedException("uName");
        
        FileAttributes fa = fs.getAttributes(ufidB);
        
        if (fa.getFileType() == FileType.DIRECTORY) {
        	// Decrements parent reference count if ufid is directory
        	fs.setAttributes(ufidDirB, new FileAttributes(null, fs.getAttributes(ufidDirB).getReferenceCount()-1, null));
        }
        
    	fs.setAttributes(ufidB, new FileAttributes(null, fa.getReferenceCount()-1, null));
        
        //Removes the fileName entry from directory. 
        Properties p = loadProperties(ufidDirB);    
        p.remove(fileName);
        storeProperties(ufidDirB, p);
        return ufidB;
    }

    @Override
    public List<String> getNames(byte[] ufidDirB, String pattern)  throws RemoteException {
        if(! AccessControl.checkPermission(new Ufid(ufidDirB).getPermission(), 'd'))
            throw new PermissionDeniedException("getNames");
        
        if (fs.getAttributes(ufidDirB).getFileType() != FileType.DIRECTORY)
        	throw new DirectoryServiceException("ufidDirB is not a directory.");
        
    	Properties p = loadProperties(ufidDirB);
    	Set<String> s = p.stringPropertyNames();
    	
    	List<String> l = new Vector<String>();
    	
    	pattern = pattern.replace("*", "\\p{Graph}*");
        pattern = pattern.replace(".", "\\.");
        pattern = pattern.replace('?', '.');
    	for(String str : s) {
    		if(str.matches(pattern)){
    			l.add(str);
    		}
    	}
    	Collections.sort(l);
        return l;
    }

    Properties loadProperties(byte[] ufidB)  throws RemoteException{
    	FileAttributes fa = fs.getAttributes(ufidB);
    	byte[] dir = fs.read(ufidB, 0l, (int)(long)fa.getFileLength());
    	Properties p = Utils.byteArrayToProperties(dir);
    	return p;
    }
    
	void storeProperties(byte[] ufidB, Properties p) throws RemoteException {
    	byte[] b = Utils.propertiesToByteArray(p);
    	// Truncates the file
		fs.truncate(ufidB, 0L);
    	fs.setAttributes(ufidB, new FileAttributes(0L, null, null));
    	fs.write(ufidB, 0, b);
    }

	
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
    	this.rootPath = rootPath;
    	File f = new File(this.rootPath);
    	f.mkdir();
	}

	public void start(String hostLigador) throws Exception{
        // Ligador location
        int portLigador = 1099;
        
        // Directory service location
        String host = RemoteUtils.getHostAddress();
        int port = 1099;
        // Gets ligador
        Ligador l = (Ligador)RemoteUtils.getRemoteObject(hostLigador, portLigador, RemoteUtils.LIGADOR);
        Service s = (Service)l.getService(RemoteUtils.FILE_SERVICE);
        
        // Gets file service
        fs = (FileService)RemoteUtils.getRemoteObject(s.getHost(), s.getPort(), RemoteUtils.FILE_SERVICE);
        
        RemoteUtils.ligadorRegistry(host, port, hostLigador, portLigador, RemoteUtils.DIR_SERVICE);

        RemoteUtils.localRegistry(port, this, RemoteUtils.DIR_SERVICE);
            
	}
	
    public static void main(String[] args) {
        try {
    		String hostLigador = (args.length == 0) ? RemoteUtils.getHostAddress() : args[0];
            DirectoryServiceImpl.getInstance().start(hostLigador); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
