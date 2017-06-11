package oldtricks.exec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import oldtricks.io.Closeables;
import oldtricks.tool.Stopwatch;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyProcessLaunchTask extends ProcessLaunchTaskImpl {

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(DummyProcessLaunchTask.class);
	private static final int DEFAULT_SLEEP_MS = 100;
	private static final int PIPE_BUFFER_SIZE = 4096;
	private InputStream stdin;
	private OutputStream stdout;
	private OutputStream stderr;
	private PipedOutputStream outerStdin;
	private PipedInputStream outerStdout;
	private PipedInputStream outerStderr;
	private String stdoutValue;
	private String stderrValue;

	private long sleep = DEFAULT_SLEEP_MS;

	private DummyHandler handler;

	public DummyProcessLaunchTask(DummyCommand dummy, ProcessIoHandler iohandler) throws IOException {
		super(dummy.getCmdline(), dummy.getEnv(), dummy.getWorkDir(), dummy.isInheritIO(), iohandler);
		this.sleep = dummy.getSleepMs();
		this.exitCode = dummy.getExitCode();
		this.handler = dummy.getHandler() != null ? dummy.getHandler() : new DefaultDummyHandler();
		this.stdoutValue = dummy.getStdoutValue();
		this.stderrValue = dummy.getStderrValue();
		if (inheritIO) {
			stdin = System.in;
			stdout = System.out;
			stderr = System.err;
		} else {
			stdin = new PipedInputStream(PIPE_BUFFER_SIZE);
			stdout = new PipedOutputStream();
			stderr = new PipedOutputStream();
			outerStdin = new PipedOutputStream((PipedInputStream) stdin);
			outerStdout = new PipedInputStream((PipedOutputStream) stdout, PIPE_BUFFER_SIZE);
			outerStderr = new PipedInputStream((PipedOutputStream) stderr, PIPE_BUFFER_SIZE);
		}
	}

	@Override
	public void run() {
		Stopwatch stopwatch = new Stopwatch().start();
		try {
			latch.countDown();
			exitCode = handler.execute(new DummyHandlerContextImpl());
			LOG.info("process finished. exitCode[" + exitCode + "], elapsed[" + stopwatch.stop() + "], " + toString());
		} catch (Exception ignore) {
			LOG.warn("An exception occured in execution process. [{}]", ignore.toString());
		} finally {
			if (inheritIO == false) {
				Closeables.closeQuietly(stdin);
				Closeables.closeQuietly(stdout);
				Closeables.closeQuietly(stderr);
			}
		}
	}

	public OutputStream getStdin() {
		return outerStdin;
	}

	public InputStream getStdout() {
		return outerStdout;
	}

	public InputStream getStderr() {
		return outerStderr;
	}

	public class DummyHandlerContextImpl implements DummyHandlerContext {
		public LineIterator readStdin(String encoding) {
			try {
				return IOUtils.lineIterator(stdin, encoding);
			} catch (IOException ignore) {
			}
			return null;
		}

		public void printout(String val) {
			try {
				IOUtils.write(val, stdout);
			} catch (IOException ignore) {
			}
		}

		public void printerr(String val) {
			try {
				IOUtils.write(val, stderr);
			} catch (IOException ignore) {
			}
		}

		public InputStream getStdin() {
			return stdin;
		}

		public String getStdoutValue() {
			return stdoutValue;
		}

		public String getStderrValue() {
			return stderrValue;
		}

		public OutputStream getStdout() {
			return stdout;
		}

		public OutputStream getStderr() {
			return stderr;
		}

		public int getExitCode() {
			return exitCode;
		}

		public long getSleepMs() {
			return sleep;
		}

		public List<String> getCmdline() {
			return cmdarray;
		}

		public File getWorkDir() {
			return workDir;
		}

		public boolean inheritIO() {
			return inheritIO;
		}

		public Map<String, String> getEnv() {
			return env;
		}
	}

	public static class DefaultDummyHandler implements DummyHandler {

		@Override
		public int execute(DummyHandlerContext ctx) throws Exception {
			ctx.printerr(">input");
			LOG.info("reading stdin:" + ctx.readStdin("UTF-8").next());
			LOG.info("sleep " + ctx.getSleepMs() + " ms");
			TimeUnit.MILLISECONDS.sleep(ctx.getSleepMs());
			ctx.printout(ctx.getStdoutValue());
			ctx.printerr(ctx.getStderrValue());
			return ctx.getExitCode();
		}

	}
}
