package dfs.exception;

public class NameDuplicateException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6212367850816112560L;

	/**
     * 
     */
    public NameDuplicateException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public NameDuplicateException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public NameDuplicateException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public NameDuplicateException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
