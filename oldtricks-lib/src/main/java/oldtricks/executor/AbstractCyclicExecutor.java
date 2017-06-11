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
package oldtricks.executor;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCyclicExecutor implements SimpleExecutor {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractCyclicExecutor.class);
	/** スレッドプール */
	private DefaultExecutor executor = new DefaultExecutor();
	/** 周期タスク */
	private Runnable task;

	public void start() throws Exception {
		executor.start();
	}

	/**
	 * ユーザータスクを呼び出します。タスク受付上限に達していた場合は、Warnログを出力して正常応答します。
	 * 
	 * @param userTask
	 *            実行するタスク
	 */
	protected final void fireTask() {
		try {
			if (task != null)
				executor.submit(task);
		} catch (Exception e) {
			LOG.warn(e.toString());
		}
	}

	@Override
	public Future<?> submit(Runnable task) throws TooManyTaskException, RejectedExecutionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void execute(Runnable command) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown(long shutdownTimeout) {
		executor.shutdown(shutdownTimeout);
	}

	@Override
	public void setThreadsSize(int threadsCore) {
		executor.setThreadsSize(threadsCore);
	}

	@Override
	public int getThreadsSize() {
		return executor.getThreadsSize();
	}

	@Override
	public void setName(String name) {
		executor.setName(name);
	}

	@Override
	public String getName() {
		return executor.getName();
	}

	public void setLimit(int limit) {
		executor.setLimit(limit);
	}

	public int getLimit() {
		return executor.getLimit();
	}

	/**
	 * 周期タスクを取得します。
	 * 
	 * @return 周期タスク
	 */
	public Runnable getTask() {
		return task;
	}

	/**
	 * 周期タスクを設定します。
	 * 
	 * @param task
	 *            周期タスク
	 */
	public void setTask(Runnable task) {
		this.task = task;
	}

}
