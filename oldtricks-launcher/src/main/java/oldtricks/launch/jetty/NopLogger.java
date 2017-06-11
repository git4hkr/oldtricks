package oldtricks.launch.jetty;

import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

public class NopLogger extends AbstractLogger {

	@Override
	public String getName() {
		return "org.eclipse.jetty.util.log";
	}

	@Override
	public void warn(String msg, Object... args) {

	}

	@Override
	public void warn(Throwable thrown) {

	}

	@Override
	public void warn(String msg, Throwable thrown) {

	}

	@Override
	public void info(String msg, Object... args) {

	}

	@Override
	public void info(Throwable thrown) {

	}

	@Override
	public void info(String msg, Throwable thrown) {

	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void setDebugEnabled(boolean enabled) {

	}

	@Override
	public void debug(String msg, Object... args) {

	}

	@Override
	public void debug(Throwable thrown) {

	}

	@Override
	public void debug(String msg, Throwable thrown) {

	}

	@Override
	public void ignore(Throwable ignored) {

	}

	@Override
	protected Logger newLogger(String fullname) {
		return new NopLogger();
	}

}
