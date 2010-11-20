package dfs.client;


import java.rmi.RemoteException;
import java.util.List;

import dfs.server.fs.FileAttributes;
import dfs.server.fs.FileAttributes.FileType;

public interface ClientModule {
	/**
	 * 
	 * @param fileUri
	 * @param uid
	 */
	public byte[] create(String fileUri, FileType ft, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param localFileUri
	 * @param remoteDirUri
	 * @param uid
	 */
	public void upload(String localFileUri, String remoteDirUri, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param localDirUri
	 * @param remoteFileUri
	 * @param uid
	 */
	public void download(String localDirUri, String remoteFileUri, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param fileUri
	 * @param uid
	 */
	public void rm(String fileUri, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param fileUri
	 * @param uidOwner
	 * @param uid
	 */
	public void chown(String fileUri, int uidOwner, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param fileUri
	 * @param permissions
	 * @param uid
	 */
	public void chmod(String fileUri, String permissions, char permissionLevel, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param fileUri
	 * @param uid
	 * @return
	 */
	public FileAttributes getAttr(byte[] ufid, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param sourceFileUri
	 * @param targetFileUri
	 * @param uid
	 */
	public void move(String sourceFileUri, String targetDirUri, int uid) throws RemoteException;
	
	/**
	 * 
	 * @param sourceFileUri
	 * @param targetDirUri
	 * @param uid
	 */
	//public void copyTo(String sourceFileUri, String targetDirUri, int uid);
	
	/**
	 * Usar rm.
	 * @param dirFileHandle
	 * @param name
	 */
	//public void rmdir(String dirUri, String name);
	
	/**
	 * Returns directory entries that matches a given pattern.
	 * @param dirUri
	 * @param pattern
	 * @param uid
	 * @return directory entries
	 */
	public List<String> ls(String dirUri, String pattern, int uid) throws RemoteException;
}
