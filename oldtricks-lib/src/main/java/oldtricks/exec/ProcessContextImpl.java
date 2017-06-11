package oldtricks.exec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oldtricks.io.Closeables;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

public class ProcessContextImpl implements ProcessContext {
	private Process process;
	/** コマンドライン文字列 */
	protected List<String> cmdarray = new ArrayList<String>();
	/** 環境変数 */
	protected Map<String, String> env;
	/** 作業ディレクトリ */
	protected File workDir;
	/** 親プロセスの標準入出力、標準エラー出力を共有する */
	protected boolean inheritIO;

	public ProcessContextImpl(Process process, List<String> cmdarray, Map<String, String> env, File workDir,
			boolean inheritIO) {
		super();
		this.process = process;
		this.cmdarray = cmdarray;
		this.env = env;
		this.workDir = workDir;
		this.inheritIO = inheritIO;
	}

	@Override
	public ProcessContext consumeStream() {
		try {
			if (getStdout() != null)
				IOUtils.copy(getStdout(), new NullOutputStream());
			if (getStderr() != null)
				IOUtils.copy(getStderr(), new NullOutputStream());
		} catch (IOException ignore) {
		}
		return this;
	}

	@Override
	public String readStdout(String encoding) throws IOException {
		return IOUtils.lineIterator(getStdout(), encoding).next();
	}

	@Override
	public String readStderr(String encoding) throws IOException {
		return IOUtils.lineIterator(getStderr(), encoding).next();
	}

	@Override
	public List<String> readStdoutByString() throws IOException {
		if (getStdout() == null)
			return new ArrayList<>();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(getStdout());
			return IOUtils.readLines(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public List<String> readStderrByString() throws IOException {
		if (getStderr() == null)
			return new ArrayList<>();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(getStderr());
			return IOUtils.readLines(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public int redirectStdoutToStream(OutputStream out, boolean withClose) throws IOException {
		try {
			if (getStdout() != null)
				return IOUtils.copy(getStdout(), out);
			else
				return 0;
		} finally {
			if (withClose)
				Closeables.closeQuietly(out);
		}
	}

	@Override
	public void redirectStdoutToStream(Writer writer, boolean withClose) throws IOException {
		try {
			if (getStdout() != null)
				IOUtils.copy(getStdout(), writer);
		} finally {
			if (withClose)
				Closeables.closeQuietly(writer);
		}
	}

	@Override
	public int redirectStderrToStream(OutputStream out, boolean withClose) throws IOException {
		try {
			if (getStderr() != null)
				return IOUtils.copy(getStderr(), out);
			else
				return 0;
		} finally {
			if (withClose)
				Closeables.closeQuietly(out);
		}
	}

	@Override
	public void redirectStderrToStream(Writer writer, boolean withClose) throws IOException {
		try {
			if (getStderr() != null)
				IOUtils.copy(getStderr(), writer);
		} finally {
			if (withClose)
				Closeables.closeQuietly(writer);
		}
	}

	@Override
	public OutputStream getStdin() {
		return process.getOutputStream();
	}

	@Override
	public InputStream getStdout() {
		return process.getInputStream();
	}

	@Override
	public InputStream getStderr() {
		return process.getErrorStream();
	}

	@Override
	public void printStdin(String val) {
		if (getStdin() == null)
			return;
		PrintWriter stdin = new PrintWriter(getStdin());
		stdin.println(val);
		stdin.flush();
	}

	/**
	 * コマンドライン文字列を取得します。
	 *
	 * @return コマンドライン文字列
	 */
	public List<String> getCmdarray() {
		return cmdarray;
	}

	/**
	 * 環境変数を取得します。
	 *
	 * @return 環境変数
	 */
	public Map<String, String> getEnv() {
		return env;
	}

	/**
	 * 作業ディレクトリを取得します。
	 *
	 * @return 作業ディレクトリ
	 */
	public File getWorkDir() {
		return workDir;
	}

	/**
	 * 親プロセスの標準入出力、標準エラー出力を共有するを取得します。
	 *
	 * @return 親プロセスの標準入出力、標準エラー出力を共有する
	 */
	public boolean isInheritIO() {
		return inheritIO;
	}

}
