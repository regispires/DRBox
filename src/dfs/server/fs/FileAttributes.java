package dfs.server.fs;
import helper.FileUtils;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;


public class FileAttributes implements Serializable {
	private static final long serialVersionUID = -2910142604568813298L;

	//private static Logger log = Logger.getLogger(FileAttributes.class.getName());
    public enum FileType  { FILE, DIRECTORY };
    
    private String fileName;
    private Long fileLength;
    private Date creationTimestamp;
    private Date readTimestamp;
    private Date writeTimestamp;
    private Date attributeTimestamp;
    private Integer referenceCount;
    private Integer owner;
    private FileType fileType;
    private Byte ownerPerm;
    private Byte groupPerm;
    private Byte otherPerm;
    
    
    public FileAttributes() {}
    
    public FileAttributes(String fileName) {
        this.fileName = fileName;
    }
    
    public FileAttributes(Long fileLength, Integer referenceCount, Integer owner) {
    	this.fileLength = fileLength;
    	this.referenceCount = referenceCount;
    	this.owner = owner;
    }
    
    /**
     * Saves a file attributes file related to an ufid.
     * @param ufid
     */
    void store(String pathName){
       Properties p = new Properties();
       if (this.fileLength != null)
           p.setProperty("fileLength", String.valueOf(this.fileLength));
       
       if(this.creationTimestamp != null)
           p.setProperty("creationTimestamp", String.valueOf(this.creationTimestamp.getTime()));
       
       if(this.readTimestamp != null)
           p.setProperty("readTimestamp", String.valueOf(this.readTimestamp.getTime()));
       
       if(this.writeTimestamp != null)
           p.setProperty("writeTimestamp", String.valueOf(this.writeTimestamp.getTime()));
       
       if (this.referenceCount != null)
           p.setProperty("referenceCount", String.valueOf(this.referenceCount));
       
       if (this.owner != null)
           p.setProperty("owner", String.valueOf(this.owner));
       
       if(this.fileType!=null)
           p.setProperty("fileType", String.valueOf(this.fileType));
       
       if(this.ownerPerm != null)
    	   p.setProperty("ownerPerm", String.valueOf(this.ownerPerm));
       
       if(this.groupPerm != null)
    	   p.setProperty("groupPerm", String.valueOf(this.groupPerm));
       
       if(this.otherPerm != null)
    	   p.setProperty("otherPerm", String.valueOf(this.otherPerm));
       
       p.setProperty("attributeTimestamp", String.valueOf(new Date().getTime()));
       FileUtils.storeProperties(pathName + "/." + fileName, p);
    }
    
    /**
     * Loads attributes files from an ufid.
     * @param ufid
     * @throws FileNotFoundException 
     */
    void load(String pathName) {
    	String fileName = pathName + "/." + this.fileName;
    	
        Properties p = FileUtils.loadProperties(fileName);
        
        String a = p.getProperty("fileLength");
        if(a != null)
            this.setFileLength(Long.parseLong(a));
        
        a=p.getProperty("creationTimestamp");
        if(a != null)
            this.setCreationTimestamp(new Date(Long.parseLong(a)));

        a = p.getProperty("readTimestamp");
        if(a != null)
            this.setReadTimestamp(new Date(Long.parseLong(a)));
        
        a = p.getProperty("writeTimestamp");
        if(a != null)
            this.setWriteTimestamp(new Date(Long.parseLong(a)));
        
        a = p.getProperty("attributeTimestamp");
        if(a != null)
            this.attributeTimestamp = new Date(Long.parseLong(a));
        
        a =p.getProperty("referenceCount");
        if(a != null)
            this.setReferenceCount(Integer.parseInt(a));
        
        a = p.getProperty("owner");
        if(a != null)
            this.setOwner(Integer.parseInt(a));
        
        a = p.getProperty("fileType");
        if(a != null)
            this.setFileType(FileType.valueOf(a));
        
        a = p.getProperty("ownerPerm");
        if(a != null)
            this.setOwnerPerm(Byte.parseByte(a));
        
        a = p.getProperty("groupPerm");
        if(a != null)
            this.setGroupPerm(Byte.parseByte(a));
        
        a = p.getProperty("otherPerm");
        if(a != null)
            this.setOtherPerm(Byte.parseByte(a));
    }
    
    public Long getFileLength() {
        return fileLength;
    }
    
    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }
    
    public Date getCreationTimestamp() {
        return creationTimestamp;
    }
    
    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
    
    public Date getReadTimestamp() {
        return readTimestamp;
    }
    
    public void setReadTimestamp(Date readTimestamp) {
        this.readTimestamp = readTimestamp;
    }
    
    public Date getWriteTimestamp() {
        return writeTimestamp;
    }
    
    public void setWriteTimestamp(Date writeTimestamp) {
        this.writeTimestamp = writeTimestamp;
    }
    
    public Date getAttributeTimestamp() {
        return attributeTimestamp;
    }
    
    public Integer getReferenceCount() {
        return referenceCount;
    }
    
    public void setReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
    }
    
    public Integer getOwner() {
        return owner;
    }
    
    public void setOwner(Integer owner) {
        this.owner = owner;
    }
    
    public FileType getFileType() {
        return fileType;
    }
    
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

	public Byte getOwnerPerm() {
		return ownerPerm;
	}

	public void setOwnerPerm(Byte ownerPerm) {
		this.ownerPerm = ownerPerm;
	}

	public Byte getGroupPerm() {
		return groupPerm;
	}

	public void setGroupPerm(Byte groupPerm) {
		this.groupPerm = groupPerm;
	}

	public Byte getOtherPerm() {
		return otherPerm;
	}

	public void setOtherPerm(Byte otherPerm) {
		this.otherPerm = otherPerm;
	}
    
}
