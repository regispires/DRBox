package dfs.client;

import dfs.Ufid;
import dfs.server.fs.FileAttributes;

public class FileInfo {
	private Ufid fileHandle;
	private FileAttributes fileAttributes;
	
	//GET's and SET's
	public Ufid getFileHandle() {
		return fileHandle;
	}
	public void setFileHandle(Ufid fileHandle) {
		this.fileHandle = fileHandle;
	}
	public FileAttributes getFileAttributes() {
		return fileAttributes;
	}
	public void setFileAttributes(FileAttributes fileAttributes) {
		this.fileAttributes = fileAttributes;
	}
	
	
}
