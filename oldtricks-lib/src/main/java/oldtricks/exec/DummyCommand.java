package oldtricks.exec;

import oldtricks.util.StringUtil;

public class DummyCommand extends Command {
	private DummyHandler handler;
	private long sleepMs;
	private int exitCode;
	private String stdoutValue;
	private String stderrValue;

	/**
	 * handlerを取得します。
	 * 
	 * @return handler
	 */
	public DummyHandler getHandler() {
		return handler;
	}

	/**
	 * handlerを設定します。
	 * 
	 * @param handler
	 *            handler
	 */
	public void setHandler(DummyHandler handler) {
		this.handler = handler;
	}

	/**
	 * sleepMsを取得します。
	 * 
	 * @return sleepMs
	 */
	public long getSleepMs() {
		return sleepMs;
	}

	/**
	 * sleepMsを設定します。
	 * 
	 * @param sleepMs
	 *            sleepMs
	 */
	public void setSleepMs(long sleepMs) {
		this.sleepMs = sleepMs;
	}

	/**
	 * exitCodeを取得します。
	 * 
	 * @return exitCode
	 */
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * exitCodeを設定します。
	 * 
	 * @param exitCode
	 *            exitCode
	 */
	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	/**
	 * stdoutValueを取得します。
	 * 
	 * @return stdoutValue
	 */
	public String getStdoutValue() {
		return stdoutValue;
	}

	/**
	 * stdoutValueを設定します。
	 * 
	 * @param stdoutValue
	 *            stdoutValue
	 */
	public void setStdoutValue(String stdoutValue) {
		this.stdoutValue = stdoutValue;
	}

	/**
	 * stderrValueを取得します。
	 * 
	 * @return stderrValue
	 */
	public String getStderrValue() {
		return stderrValue;
	}

	/**
	 * stderrValueを設定します。
	 * 
	 * @param stderrValue
	 *            stderrValue
	 */
	public void setStderrValue(String stderrValue) {
		this.stderrValue = stderrValue;
	}

	@Override
	public String toString() {
		return "DummyCommand [handler=" + handler + ", sleepMs=" + sleepMs + ", exitCode=" + exitCode + ", stdout=\""
				+ StringUtil.abbreviate(stdoutValue, 10) + "\", stderr=\"" + StringUtil.abbreviate(stderrValue, 10)
				+ "\"]";
	}

}
