package oldtricks.exec;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.LineIterator;

public interface DummyHandlerContext {
	public LineIterator readStdin(String encoding);

	public void printout(String val);

	public void printerr(String val);

	public InputStream getStdin();

	public OutputStream getStdout();

	public OutputStream getStderr();

	public int getExitCode();

	public long getSleepMs();

	public String getStdoutValue();

	public String getStderrValue();

	public List<String> getCmdline();

	public File getWorkDir();

	public boolean inheritIO();

	public Map<String, String> getEnv();
}
