package oldtricks.exec;

public class IllegalExitStateException extends Exception {

	private static final long serialVersionUID = -3871917997723955866L;
	private int exitCode = 0;
	private SubProcess process;

	public int getExitCode() {
		return exitCode;
	}

	public IllegalExitStateException(int exit) {
		super("exitCode=" + exit + "]");
		exitCode = exit;
	}

	public IllegalExitStateException(SubProcess process) {
		super("exitCode=" + process.getExitCode() + ", process=" + process + "]");
		this.process = process;
		this.exitCode = process.getExitCode();
	}

	@Override
	public String toString() {
		return "IllegalExitStateException [exitCode=" + exitCode + ", process=" + process + "]";
	}

}
