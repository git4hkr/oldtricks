package oldtricks.exec.builder;

import oldtricks.exec.Command;
import oldtricks.exec.DummyCommand;
import oldtricks.exec.DummyHandler;
import oldtricks.util.StringUtil;

public class DummyCmdBuilder extends DefaultCmdBuilder {

	private DummyHandler handler;
	private long sleepMs;
	private int exitCode;
	private String stdoutValue;
	private String stderrValue;

	public static DummyCmdBuilder create() {
		return new DummyCmdBuilder();
	}

	public DummyCmdBuilder addHandler(DummyHandler handler) {
		this.handler = handler;
		return this;
	}

	public DummyCmdBuilder sleepMs(long sleepMs) {
		this.sleepMs = sleepMs;
		return this;
	}

	public DummyCmdBuilder exitCode(int exitCode) {
		this.exitCode = exitCode;
		return this;
	}

	public DummyCmdBuilder stdoutValue(String value) {
		this.stdoutValue = StringUtil.defaultIfEmpty(value, "");
		return this;
	}

	public DummyCmdBuilder stderrValue(String value) {
		this.stderrValue = StringUtil.defaultIfEmpty(value, "");
		return this;
	}

	@Override
	protected Command createInstance() {
		return new DummyCommand();
	}

	public Command build() {
		DummyCommand command = (DummyCommand) super.build();
		command.setSleepMs(sleepMs);
		command.setExitCode(exitCode);
		command.setHandler(handler);
		command.setStdoutValue(stdoutValue);
		command.setStderrValue(stderrValue);
		return command;
	}
}
