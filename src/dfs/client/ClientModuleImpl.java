package dfs.client;

import helper.RemoteUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import ligador.Ligador;
import ligador.Service;

import dfs.AccessControl;
import dfs.exception.DirectoryServiceException;
import dfs.exception.OperationNotCompletedException;
import dfs.exception.PermissionDeniedException;
import dfs.server.ds.DirectoryService;
import dfs.server.fs.FileAttributes;
import dfs.server.fs.FileAttributes.FileType;
import dfs.server.fs.FileService;

public class ClientModuleImpl implements ClientModule {
	//Verifies this for many applications
	private boolean cancel;
	
	private static ClientModuleImpl cm;
    static DirectoryService ds;
    static FileService fs;

    private static final int UFID_PERMISSION_BYTE = 32; 
    private static final int BLOCK_SIZE = 2048;
    
    private ClientModuleImpl() throws RemoteException {
    }

    public static ClientModuleImpl getInstance() throws RemoteException {
    	if(cm == null)
    		cm = new ClientModuleImpl();
    	return cm ;
    }
    
    byte[] getUfdi(String uri, int uid) throws RemoteException{
        byte[] ufid = ds.lookup(null, null, uid);
        String[] dirs = uri.split("/");
        for (int i=0; i < dirs.length; i++) {
            if (! dirs[i].equals("")) {
                ufid = ds.lookup(ufid, dirs[i], uid);
            }
        }
        if (! AccessControl.checkPermission(ufid[UFID_PERMISSION_BYTE], 'r'))
            throw new PermissionDeniedException("getUfid");
        return ufid;
    }

    public String[] splitUri(String fileUri) {
        int pos = fileUri.lastIndexOf('/');
        String uri = fileUri.substring(0, pos);
        String fileName = fileUri.substring(pos+1);
        String[] result = {uri, fileName};
        return result; 
    }
    
    @Override
    public byte[] create(String fileUri, FileType ft, int uid) throws RemoteException{
        String[] splitedUri = splitUri(fileUri); 
        String fileName = splitedUri[1];
        byte[] ufidDir = getUfdi(splitedUri[0], uid);

        if (! AccessControl.checkPermission(ufidDir[UFID_PERMISSION_BYTE], 'w'))
            throw new PermissionDeniedException("create (ClientModule)");
        
        byte[] ufid = null;
        try {
            ufid = fs.create(ft);
            ufid = ds.addName(ufidDir, fileName, ufid, uid);
            if(ft.equals(FileType.DIRECTORY)){
            	
            	// Look up to get capacity
            	ufid = ds.lookup(ufidDir, fileName, uid);
            	ds.addName(ufid, "..", ufidDir, uid);
            }
        } catch (PermissionDeniedException e) {
            fs.delete(ufid);
        }
        return ufid;
    }

    public void upload(String localFileUri, String remoteDirUri, int uid) throws RemoteException{
    	this.cancel = false;
        File f = new File(localFileUri);
        
        if (! f.isFile())
            throw new IllegalArgumentException("Cannot upload a directory.");
        
        String remoteFileUri = remoteDirUri + "/" + f.getName();
        byte[] ufid = create(remoteFileUri, FileType.FILE, uid);
        
        int i = 0;
        byte[] data = new byte[BLOCK_SIZE];
        try {
            FileInputStream in = new FileInputStream(f);
            
            int readBytes;
            while ((readBytes = in.read(data)) != -1 && !cancel) {
                if (readBytes == BLOCK_SIZE) {
                    fs.write(ufid, i, data);
                } else {
                    fs.write(ufid, i, Arrays.copyOf(data, readBytes));
                }
                i = i + readBytes;
            }
            if(cancel){
            	rm(remoteFileUri, uid);
            	throw new OperationNotCompletedException("Upload canceled.");
            }
        } catch (IOException e) {
            // Undo operation
            rm(remoteFileUri, uid);
            throw new OperationNotCompletedException(e.getMessage(), e);
        }finally{
        	cancel = false;
        }
    }
    
    public void download(String localDirUri, String remoteFileUri, int uid) throws RemoteException{
    	this.cancel = false;
        File f = new File(localDirUri);
        
        if (! f.isDirectory())
            throw new IllegalArgumentException("Cannot download to a file.");
        
        String[] splited = splitUri(remoteFileUri);
        
        String localFileUri = localDirUri + "/" + splited[1];
        byte[] ufid = getUfdi(remoteFileUri, uid);
        if(getAttr(ufid, uid).getFileType() == FileType.DIRECTORY)
        	throw new IllegalArgumentException("Cannot download a directory.");
        
        //Creates a file with same name of the remote file.
        f = new File(localFileUri);
        
        try {
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
           
            long fileLength = getAttr(ufid, uid).getFileLength();
            int n = (int)(fileLength / BLOCK_SIZE);
            
            for(int i=0; i<=n && !cancel; i++){
                byte[] data = fs.read(ufid, i*BLOCK_SIZE, BLOCK_SIZE);
                out.write(data);
            }
            if(cancel){
            	f.delete();
            	throw new OperationNotCompletedException("Download canceled.");
            }
        } catch (IOException e) {
            // Undo operation
            f.delete();
            throw new OperationNotCompletedException(e.getMessage(), e);
        }finally{
        	cancel = false;
        }
    }
    
    @Override
    public void rm(String fileUri, int uid)  throws RemoteException{
        String[] splitedUri = splitUri(fileUri);
        byte[] ufidDir = getUfdi(splitedUri[0], uid);
        
        if (! AccessControl.checkPermission(ufidDir[UFID_PERMISSION_BYTE], 'd'))
            throw new PermissionDeniedException("rm (ClientModule)");

        byte[] ufid = getUfdi(fileUri, uid);
        FileAttributes fa = fs.getAttributes(ufid);
        if (fa.getFileType() == FileType.DIRECTORY){
        	if(ds.getNames(ufid, "*").size() > 1)
        		throw new OperationNotCompletedException("Cannot remove a not empty directory.");
        }
        
        
        // unName if not root dir
        if (! splitedUri[1].equals(""))
            ds.unName(ufidDir, splitedUri[1], uid);
        else
        	throw new OperationNotCompletedException("Cannot remove root directory.");
        
        fs.delete(ufid);
    }

    @Override
    public void chown(String fileUri, int uidOwner, int uid) throws RemoteException {
        byte[] ufid = getUfdi(fileUri, uid);
        
        if (! AccessControl.checkPermission(ufid[UFID_PERMISSION_BYTE], 's'))
            throw new PermissionDeniedException("chown");
        
        FileAttributes fa = getAttr(ufid, uid);
        fa.setOwner(uidOwner);
        fs.setAttributes(ufid, fa);
    }

    @Override
    public void chmod(String fileUri, String permissions, char permissionLevel, int uid) throws RemoteException {
        byte[] ufid = getUfdi(fileUri, uid);
        
        FileAttributes fa = getAttr(ufid, uid);
        switch (permissionLevel) {
        case 'u':
            fa.setOwnerPerm(AccessControl.getPermission(permissions));
            break;
        case 'g':
            fa.setGroupPerm(AccessControl.getPermission(permissions));
            break;
        case 'o':
            fa.setOtherPerm(AccessControl.getPermission(permissions));
            break;
        case 'a':
            fa.setOwnerPerm(AccessControl.getPermission(permissions));
            fa.setGroupPerm(AccessControl.getPermission(permissions));
            fa.setOtherPerm(AccessControl.getPermission(permissions));
            break;
        default:
            break;
        }
    }

    @Override
    public FileAttributes getAttr(byte[] ufid, int uid) throws RemoteException{
        if (! AccessControl.checkPermission(ufid[UFID_PERMISSION_BYTE], 'g'))
            throw new PermissionDeniedException("getAttr");
        
        return fs.getAttributes(ufid);
    }


    @Override
    public void move(String sourceFileUri, String targetDirUri, int uid)  throws RemoteException{
        byte[] sourceUfid = getUfdi(sourceFileUri, uid);
        byte[] targetUfid = getUfdi(targetDirUri, uid);
        
        if (! AccessControl.checkPermission(sourceUfid[UFID_PERMISSION_BYTE], 'd'))
            throw new PermissionDeniedException("move (ClientModule)");
        
        if (! AccessControl.checkPermission(targetUfid[UFID_PERMISSION_BYTE], 'w'))
            throw new PermissionDeniedException("move (ClientModule)");

        String[] splitedSourceUri = splitUri(sourceFileUri);
        byte[] sourceDirUfid = getUfdi(splitedSourceUri[0], uid);
        
        ds.unName(sourceDirUfid, splitedSourceUri[1], uid);
        ds.addName(targetUfid, splitedSourceUri[1], sourceUfid, uid);
    }
    
    /*
    @Override
    public void copyTo(String sourceFileUri, String targetDirUri, int uid) {
        byte[] sourceUfid = getUfdi(sourceFileUri, uid);
        byte[] targetUfid = getUfdi(targetDirUri, uid);
        
        if (! AccessControl.checkPermission(sourceUfid[UFID_PERMISSION_BYTE], 'r'))
            throw new PermissionDeniedException("copyTo (ClientModule)");
        
        if (! AccessControl.checkPermission(targetUfid[UFID_PERMISSION_BYTE], 'w'))
            throw new PermissionDeniedException("copyTo (ClientModule)");

        String[] splitedSourceUri = splitUri(sourceFileUri);
        ds.addName(targetUfid, splitedSourceUri[1], sourceUfid, uid);
    }
    */

    @Override
    public List<String> ls(String dirUri, String pattern, int uid) throws RemoteException {
        byte[] ufidDir = getUfdi(dirUri, uid);
        List<String> result = null;
        
        if (! AccessControl.checkPermission(ufidDir[UFID_PERMISSION_BYTE], 'r'))
            throw new PermissionDeniedException("ls (ClientModule)");
        try {
        	result = ds.getNames(ufidDir, pattern);
		} catch (DirectoryServiceException e) {
			String[] uri = splitUri(dirUri);
			throw new IllegalArgumentException(uri[1]+" is not a directory.");
		}
        
        return result;
    }
    
    public void cancelOperation(){
    	cancel = true;
    }
    

    public void start(String hostLigador) throws Exception{
        // Ligador location
        //String hostLigador = RemoteUtils.getHostAddress();
        int portLigador = 1099;
        
        Ligador l = (Ligador)RemoteUtils.getRemoteObject(hostLigador, portLigador, RemoteUtils.LIGADOR);
        
        Service fServ = l.getService(RemoteUtils.FILE_SERVICE);
        fs = (FileService)RemoteUtils.getRemoteObject(fServ.getHost(), fServ.getPort(), RemoteUtils.FILE_SERVICE);
        
        Service dServ = l.getService(RemoteUtils.DIR_SERVICE);            
        ds = (DirectoryService)RemoteUtils.getRemoteObject(dServ.getHost(), dServ.getPort(), RemoteUtils.DIR_SERVICE);

        System.out.println(this.ls("/", "*", 0));
            
        
    }
    
    public static void main(String[] args) {
        try {
    		String hostLigador = (args.length == 0) ? RemoteUtils.getHostAddress() : args[0];
            ClientModuleImpl.getInstance().start(hostLigador);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
