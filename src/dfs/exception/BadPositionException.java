package dfs.exception;

public class BadPositionException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -842482894248213417L;

	/**
     * 
     */
    public BadPositionException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public BadPositionException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public BadPositionException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public BadPositionException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
