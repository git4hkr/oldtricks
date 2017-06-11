package oldtricks.exec.builder;

public class CommandBuildingException extends RuntimeException {

	private static final long serialVersionUID = 6684677444766955932L;

	public CommandBuildingException() {
		super();
	}

	public CommandBuildingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommandBuildingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandBuildingException(String message) {
		super(message);
	}

	public CommandBuildingException(Throwable cause) {
		super(cause);
	}

}
