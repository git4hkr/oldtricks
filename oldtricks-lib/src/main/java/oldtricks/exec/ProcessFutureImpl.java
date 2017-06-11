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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProcessFutureImpl implements SubProcess {
	private Future<?> future;
	private ProcessLaunchTask cliTask;

	public ProcessFutureImpl(Future<?> future, ProcessLaunchTask cliTask) {
		super();
		this.future = future;
		this.cliTask = cliTask;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	/**
	 * サブプロセスが終了するまでスレッドをブロックします。サブプロセス終了後にexitコードを返却します。<BR>
	 * <b>【注意！！】</b>サブプロセスが大量の出力を標準出力や標準エラー出力に出力した場合、そのストリームを消費しないと
	 * バッファが空くまでサブプロセスの処理がブロックされ、永久に完了しません。本関数を呼び出す前に 標準出力{@link #getStdout()}
	 * と標準エラー出力 {@link #getStderr()} で得られるストリームを消費し終えるか、{@link #consumeStream()}
	 * を呼び出すことを強くおすすめします。
	 */
	@Override
	public Integer get() throws InterruptedException, ExecutionException {
		future.get();
		return cliTask.getExitCode();
	}

	/**
	 * サブプロセスが終了するまで指定された時間スレッドをブロックします。サブプロセス終了後にexitコードを返却します。<BR>
	 * <b>【注意！！】</b>サブプロセスの標準出力や標準エラー出力のストリームを消費しないと、
	 * 標準出力のバッファが空くまでサブプロセスの処理がブロックされ
	 * 、永久に完了しないことがあります。本関数を呼び出す前に標準出力と標準エラー出力を消費し終えるか、{@link #consumeStream()}
	 * を呼び出すことを強くおすすめします。
	 */
	@Override
	public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		future.get(timeout, unit);
		return cliTask.getExitCode();
	}

	@Override
	public boolean awaitLaunchProcess(long timeout, TimeUnit unit) throws InterruptedException {
		if (isDone())
			return false;
		return !cliTask.awaitStartProcess(timeout, unit);
	}

	@Override
	public SubProcess awaitCompletion(long timeout) throws ExecutionException, InterruptedException, TimeoutException {
		try {
			if (timeout == 0)
				get();
			else
				get(timeout, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			cancel(true);
			throw new TimeoutException("timeout occured. over " + timeout + "sec, -> " + toString());
		}
		return this;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see oldtricks.exec.ProcessFuture#getExitCode()
	 */
	@Override
	public int getExitCode() {
		return cliTask.getExitCode();
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see oldtricks.exec.ProcessFuture#geWorkDir()
	 */
	@Override
	public File geWorkDir() {
		return cliTask.getWorkDir();
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see oldtricks.exec.ProcessFuture#destroy()
	 */
	@Override
	public void destroy() {
		cancel(true);
		cliTask.destroy();
	}

	@Override
	public String toString() {
		return cliTask.toString();
	}

}