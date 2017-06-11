/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package oldtricks.exec;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import oldtricks.tool.Stopwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessLaunchTaskImpl implements ProcessLaunchTask {

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(ProcessLaunchTaskImpl.class);
	/** コマンドライン文字列 */
	protected List<String> cmdarray = new ArrayList<String>();
	/** 環境変数 */
	protected Map<String, String> env;
	/** 作業ディレクトリ */
	protected File workDir;
	/** 親プロセスの標準入出力、標準エラー出力を共有する */
	protected boolean inheritIO;
	/** 子プロセスのexitコード */
	protected int exitCode = 0;
	/** プロセスの入出力ハンドラー */
	protected ProcessIoHandler ioHandler;
	/** 子プロセス */
	private Process process;
	protected CountDownLatch latch = new CountDownLatch(1);
	private boolean destroyed;

	public ProcessLaunchTaskImpl() {

	}

	/**
	 * 外部コマンド実行タスクです。
	 * 
	 * @param cmdline
	 *            実行するコマンドを指定します。
	 * @param env
	 *            環境変数。NULLの場合、実行元の環境変数を引き継ぎます。
	 * @param workDir
	 *            作業ディレクトリ。NULLの場合、実行元の作業ディレクトリを引き継ぎます。
	 * @param inheritIO
	 *            親プロセスの標準入出力、標準エラー出力を共有する場合はtrueを指定します。デフォルトはtrueです。
	 * @param ioHandler
	 *            プロセスのIOハンドラー
	 */
	public ProcessLaunchTaskImpl(String[] cmdline, Map<String, String> env, File workDir, boolean inheritIO,
			ProcessIoHandler ioHandler) {
		super();
		this.cmdarray.addAll(Arrays.asList(cmdline));
		this.env = env;
		this.workDir = workDir;
		this.inheritIO = inheritIO;
		this.ioHandler = ioHandler;
	}

	@Override
	public void run() {
		Stopwatch stopwatch = new Stopwatch().start();
		try {
			ProcessBuilder pb = new ProcessBuilder(cmdarray);
			if (env != null)
				pb.environment().putAll(env);
			if (inheritIO)
				pb.inheritIO();
			process = pb.directory(workDir).start();
			latch.countDown();
			if (ioHandler != null)
				ioHandler.handleIo(new ProcessContextImpl(process, cmdarray, env, workDir, inheritIO));
			exitCode = process.waitFor();
			LOG.info("process finished. exitCode[" + exitCode + "], elapsed[" + stopwatch.stop() + "], " + toString());
		} catch (Exception ignore) {
			LOG.warn("An exception occured in execution process. [{}]", ignore.toString());
			destroy();
		}
	}

	/**
	 * 外部プロセスの起動を待ちます。<br>
	 * <b>[注意!!]</b> もしタスクが実行前にキャンセルされた場合、かならずタイムアウトし、永久にfalseを返却し続けます。
	 * 
	 * @param timeout
	 *            unitで指定した粒度で待つ時間を指定します。
	 * @param unit
	 *            待ち時間の粒度を指定します。
	 * @return プロセスが起動済み、もしくは起動に失敗した場合はtrue。未起動の場合はfalse。
	 * @throws InterruptedException
	 *             待っている間にスレッドが割り込みされた場合
	 */
	@Override
	public boolean awaitStartProcess(long timeout, TimeUnit unit) throws InterruptedException {
		return latch.await(timeout, unit);
	}

	/**
	 * 子プロセスを終了します。
	 */
	@Override
	public synchronized void destroy() {
		if (process != null) {
			if (destroyed == false) {
				process.destroy();
				latch.countDown();
				destroyed = true;
				LOG.info("destroyed process. " + toString());
			}
		}
	}

	/**
	 * 子プロセスのexitコードを取得します。
	 * 
	 * @return 子プロセスのexitコード
	 */
	@Override
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * 作業ディレクトリを取得します。
	 * 
	 * @return 作業ディレクトリ
	 */
	@Override
	public File getWorkDir() {
		return workDir;
	}

	@Override
	public String toString() {
		return "[cmd=[" + cmdarrayToString() + "], env=" + env + ", workDir=" + workDir + ", inheritIO=" + inheritIO
				+ "]";
	}

	private String cmdarrayToString() {
		StringBuilder builder = new StringBuilder();
		for (String param : cmdarray) {
			if (param != null && param.contains(" "))
				builder.append("'" + param + "'");
			else
				builder.append(param);
			builder.append(" ");
		}
		return builder.toString();
	}
}
