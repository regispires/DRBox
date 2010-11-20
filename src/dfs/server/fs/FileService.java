package dfs.server.fs;


import java.rmi.Remote;
import java.rmi.RemoteException;

import dfs.server.fs.FileAttributes.FileType;

public interface FileService extends Remote {

    /**
     * Copia para data a sequencia de n elementos de dados a
     * partir do elemento i do arquivo especificado.  
     * @param ufid - unique file identifier
     * @param i - initial position
     * @param n - number of elements to be read
     * @return data
     */
    public byte[] read(byte[] ufid, long i, int n) throws RemoteException;

    /**
     * Escreve a partir do elemento i a sequencia de elementos de
     * dados em data para o arquivo especificado, substituindo o 
     * conteúdo anterior do arquivo na posição correspondente e se
     * necessário, aumentando o arquivo. 
     * @param ufid - unique file identifier
     * @param i - initial position
     * @param data - data to be written
     */
    public void write(byte[] ufid, long i, byte[] data) throws RemoteException;
    
    /**
     * Creates a file and returns ufid 
     * @return ufid
     */
    public byte[] create() throws RemoteException;

    /**
     * Creates a file with an specified file type and returns ufid 
     * @param fileType
     * @return ufid
     */
    public byte[] create(FileType ft) throws RemoteException;
    
    /**
     * Deletes a file identified by ufid
     * @param ufid
     */
    public void delete(byte[] ufid) throws RemoteException;
    
    /**
     * Truncates the file 
     * @param ufid
     * @param length
     */
    public void truncate(byte[] ufid, long length) throws RemoteException;
    
    /**
     * Returns file attributes specified by a ufid
     * @param ufid
     * @return File attributes  
     */
    public FileAttributes getAttributes(byte[] ufid) throws RemoteException;
    
    /**
     * Changes file attributes from a specified ufid.
     * This method only can change owner, file type and access control list
     * attributes.
     * @param ufid
     * @param fileAttributes
     */
    public void setAttributes(byte[] ufid, FileAttributes fileAttributes) throws RemoteException;
    
}