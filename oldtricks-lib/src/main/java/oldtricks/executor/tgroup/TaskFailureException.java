package oldtricks.executor.tgroup;

import java.util.concurrent.Callable;

public class TaskFailureException extends Exception {
	private static final long serialVersionUID = 7106577998401400253L;
	private Callable<?> failureTask;

	public Callable<?> getFailureTask() {
		return failureTask;
	}

	public TaskFailureException(Throwable cause, Callable<?> failureTask) {
		super(cause);
		this.failureTask = failureTask;
	}

	@Override
	public String toString() {
		return "TaskRunningException [failureTask -> " + failureTask + ", cause=" + getCause() + "]";
	}

}