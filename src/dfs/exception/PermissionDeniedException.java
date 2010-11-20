package dfs.exception;

public class PermissionDeniedException extends RuntimeException {

	private static final long serialVersionUID = 7391142828890835535L;
	
	/**
     * 
     */
    public PermissionDeniedException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

  
    /**
     * @param message
     */
    public PermissionDeniedException(String operation) {
        super("User do not have permission to '"+operation+"' operation.");
        // TODO Auto-generated constructor stub
    }
    /**
     * @param cause
     */
    public PermissionDeniedException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
