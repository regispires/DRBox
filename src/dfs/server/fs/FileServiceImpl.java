package dfs.server.fs;
import helper.FileUtils;
import helper.RemoteUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;


import dfs.AccessControl;
import dfs.Ufid;
import dfs.exception.BadPositionException;
import dfs.exception.FileServiceException;
import dfs.exception.PermissionDeniedException;
import dfs.server.fs.FileAttributes.FileType;

public class FileServiceImpl extends UnicastRemoteObject implements FileService {
	private static final long serialVersionUID = 3601722609574349519L;
	
	private static FileServiceImpl fs;
	private static Logger log = Logger.getLogger(FileServiceImpl.class.getName());
    public static String fileServiceHost = RemoteUtils.getHostAddress(); 
    private String rootPath;
    

    private FileServiceImpl() throws RemoteException {
    	setRootPath(System.getProperty("user.home")+"/dfs");
    }
    
    public static FileServiceImpl getInstance() throws RemoteException{
    	if(fs == null)
    		fs = new FileServiceImpl();
    	return fs;
    }
    
    @Override
    public byte[] read(byte[] ufidB, long i, int n) {
        Ufid ufid = new Ufid(ufidB);
        
        if(! AccessControl.checkPermission(ufid.getPermission(), 'r'))
            throw new PermissionDeniedException("read");
        
        File f = new File(rootPath+"/"+ufid.toString());
        FileAttributes fa = this.getAttributes(ufidB);
        long remainingBytes = fa.getFileLength() - i;
        int min = (n < remainingBytes) ? n : (int)remainingBytes;
        
        byte[] result = new byte[min];
        RandomAccessFile in = null;
        try {
            in = new RandomAccessFile(f, "r");
            in.seek(i);
            in.readFully(result);
            log.fine("read: " + f.getName() + "\n"+ new String(result));
            fa.setReadTimestamp(new Date());
            setAllAttributes(ufidB, fa);
        } catch (EOFException e) {
        	throw new FileServiceException(new BadPositionException(e.getMessage(), e));
        }catch (FileNotFoundException e) {
        	throw new FileServiceException(e.getMessage(),e);
        }catch (IOException e) {
        	throw new FileServiceException(e.getMessage(),e);
        } finally {
        	if (in != null) {
        		try {
        			in.close();
        		} catch (IOException e) {
        			throw new FileServiceException(e.getMessage(),e);
        		}
        	}
        }
        return result;
    }

    @Override
    public void write(byte[] ufidB, long i, byte[] data) throws RemoteException{
        Ufid ufid = new Ufid(ufidB);
        if(! AccessControl.checkPermission(ufid.getPermission(), 'w'))
            throw new PermissionDeniedException("write");
        
        File f = new File(rootPath+"/"+ufid.toString());
        log.fine("write: " + f.getName() + "\n"+ new String(data));
        RandomAccessFile out = null;
        try {
        	long length = f.length();
        	if (i > length) {
                throw new FileServiceException(new BadPositionException("Invalid position: " + i));
        	}
            out = new RandomAccessFile(f,"rw");
            out.seek(i);
            out.write(data);
            
            FileAttributes fa = new FileAttributes();
            fa.setWriteTimestamp(new Date());
            fa.setFileLength(f.length());
            setAllAttributes(ufidB, fa);
            
        } catch (IOException e) {
        	throw new FileServiceException(e.getMessage(),e);
        } finally {
        	if (out != null) {
        		try {
					out.close();
				} catch (IOException e) {
					throw new FileServiceException(e.getMessage(),e);
				}
        	}
        }
    }

    @Override
    public byte[] create() {
        return this.create(FileType.FILE);
    }
    
    @Override
    public byte[] create(FileType ft) {
        Ufid ufid = new Ufid();
        String ufidStr = ufid.toString();
        log.fine("Ufid: " + ufidStr);
        File f = new File(rootPath+"/"+ufidStr);
        byte[] ufidB = ufid.getBytes();
        try {
            f.createNewFile();
            
            FileAttributes fa = new FileAttributes();
            fa.setFileLength(0L);
            fa.setCreationTimestamp(new Date());
            fa.setFileType(ft);
            fa.setReferenceCount(0);
            fa.setOwnerPerm(AccessControl.DEFAULT_OWNER_PERMISSION);
            fa.setGroupPerm(AccessControl.DEFAULT_GROUP_PERMISSION);
            fa.setOtherPerm(AccessControl.DEFAULT_OTHER_PERMISSION);
            setAllAttributes(ufidB, fa);
            
            log.fine(ufidStr+" file successful created.");
        } catch (IOException e) {
        	throw new FileServiceException(e.getMessage(),e);
        }
        return ufidB;
    }

    @Override
    public void delete(byte[] ufidB) {
        Ufid ufid = new Ufid(ufidB);
        String ufidStr = ufid.toString();
        File f = new File(rootPath+"/"+ufidStr);
        
        // Allows deletion of recently created files (ufid without encryption)
        if (ufidB.length != Ufid.UFID_DEFAULT_LENGTH || f.length() != 0)
            if(! AccessControl.checkPermission(ufid.getPermission(), 'd'))
                throw new PermissionDeniedException("delete");
        
        boolean deleted = f.delete();
        if(deleted)
            log.fine(ufidStr+" file successful deleted.");
        else
            throw new FileServiceException(ufidStr + " file could not be deleted.");
        
        f = new File(rootPath+"/"+"."+ufidStr);
        deleted = f.delete();
        if(deleted)
            log.fine(ufidStr+" file attributes successful deleted.");
        else
            throw new FileServiceException(ufidStr + " file attributes could not be deleted.");
    }

    @Override
    public void truncate(byte[] ufidB, long length) {
        Ufid ufid = new Ufid(ufidB);
        if(! AccessControl.checkPermission(ufid.getPermission(), 'w'))
            throw new PermissionDeniedException("truncate");
        
		try {
			File f = new File(rootPath+"/"+ufid.toString());
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.setLength(length);
			FileAttributes fa = new FileAttributes();
			fa.setWriteTimestamp(new Date());
			fa.setFileLength(length);
			setAllAttributes(ufidB, fa);
		} catch (IOException e) {
			throw new FileServiceException(e.getMessage(), e);
		} 
    }    
    
    /*
    public byte[] copy(byte[] ufidB) {
        Ufid ufid = new Ufid(ufidB);
        String ufidStr = ufid.toString();
        
        Ufid newUfid = new Ufid();
        String newUfidStr = newUfid.toString();
    }
    */  
    
    @Override
    public FileAttributes getAttributes(byte[] ufidB) {
        Ufid ufid = new Ufid(ufidB);
        if(! AccessControl.checkPermission(ufid.getPermission(), 'g'))
            throw new PermissionDeniedException("getAttributes");
        
    	String ufidStr = ufid.toString();
        String fileName = rootPath + "/." + ufidStr; 
        
        File f = new File(fileName);
    	if (! f.exists())
    		throw new FileServiceException("Could not find attribute file: ." + ufidStr);

        FileAttributes fa = new FileAttributes(ufidStr);
    	fa.load(rootPath);
        return fa;
    }

    private void setAllAttributes(byte[] ufidB, FileAttributes fa) {
	    Ufid ufid = new Ufid(ufidB);
	    
        FileAttributes fa2 = new FileAttributes(ufid.toString());
    	fa2.load(rootPath);
        
        if ((fa2.getFileLength() != null && fa2.getFileLength() != 0) 
        		|| (fa2.getReferenceCount() != null && fa2.getReferenceCount() != 0)) {
        	
	        if(! AccessControl.checkPermission(ufid.getPermission(), 's')) {
	        	throw new PermissionDeniedException("setAttributes");
	        }
        }

		if(fa.getFileLength() != null)
			fa2.setFileLength(fa.getFileLength());

		if(fa.getReadTimestamp() != null)
			fa2.setReadTimestamp(fa.getReadTimestamp());
		
		if(fa.getCreationTimestamp() != null)
			fa2.setCreationTimestamp(fa.getCreationTimestamp());
        
		if(fa.getWriteTimestamp() != null)
			fa2.setWriteTimestamp(fa.getWriteTimestamp());
		
        if(fa.getOwner() != null)
            fa2.setOwner(fa.getOwner());
        
        if(fa.getFileType() != null)
            fa2.setFileType((FileType)fa.getFileType());
        
        if(fa.getFileType() != null)
            fa2.setFileType((FileType)fa.getFileType());
        
        if(fa.getReferenceCount() != null)
            fa2.setReferenceCount(fa.getReferenceCount());
        
        if(fa.getOwnerPerm() != null)
        	fa2.setOwnerPerm(fa.getOwnerPerm());
        
        if(fa.getGroupPerm() != null)
        	fa2.setGroupPerm(fa.getGroupPerm());
        
        if(fa.getOtherPerm() != null)
        	fa2.setOtherPerm(fa.getOtherPerm());
        
        fa2.store(rootPath);
    }
    
	@Override
    public void setAttributes(byte[] ufidB, FileAttributes fa) {
		fa.setFileLength(null);
		fa.setReadTimestamp(null);
		fa.setCreationTimestamp(null);
		fa.setWriteTimestamp(null);
		
		setAllAttributes(ufidB, fa);
    }
	
	//----------------Gets and Sets--------------------
    //BEGIN
    public int getFileNumber() {
    	Properties p = null;
		p = FileUtils.loadProperties(rootPath + "/file_number");
    	int fileNumber;
    	if (p.getProperty("number") == null) {
    		fileNumber = 1;
    	} else {
    		fileNumber = Integer.parseInt(p.getProperty("number")) + 1;
    	}
    	p.setProperty("number", String.valueOf(fileNumber));
    	FileUtils.storeProperties(rootPath + "/file_number", p);
        return fileNumber;
    }
    
    public String getRootPath() {
    	return rootPath;
    }
    
    public void setRootPath(String rootPath) {
    	this.rootPath = rootPath;
    	File f = new File(this.rootPath);
    	f.mkdir();
    }
    
    //END
    //-------------------------------------------------

	public void start(String hostLigador) {
        // Ligador location
        int portLigador = 1099;
        
        // File service location
        String host = RemoteUtils.getHostAddress();
        int port = 1099;
        RemoteUtils.ligadorRegistry(host, port, hostLigador, portLigador, RemoteUtils.FILE_SERVICE);
        RemoteUtils.localRegistry(port, this, RemoteUtils.FILE_SERVICE);
    }

    public static void main(String[] args) {
        try {
    		String hostLigador = (args.length == 0) ? RemoteUtils.getHostAddress() : args[0];
            FileServiceImpl.getInstance().start(hostLigador);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
