package dfs.exception;

public class OperationNotCompletedException extends RuntimeException {

	private static final long serialVersionUID = -8064859622149238531L;

	public OperationNotCompletedException() {
        // TODO Auto-generated constructor stub
    }

    public OperationNotCompletedException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public OperationNotCompletedException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public OperationNotCompletedException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
