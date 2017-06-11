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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import oldtricks.util.Assert;

/**
 * シンプルな同時受付数上限つきExecutorです。
 *
 * @author $Author: kubota $
 *
 */
public class DefaultExecutor extends ExecutorSupport {
	private static final long serialVersionUID = -1769368222385589663L;
	private Semaphore taskSemaphore;
	/** 同時受付数上限値 */
	private int limit = Integer.MAX_VALUE;

	/**
	 * コンストラクタ
	 *
	 * @param name
	 *            スレッドプール名
	 * @param threads
	 *            スレッド数
	 * @param limit
	 *            同時受付上限数
	 */
	public DefaultExecutor() {
		super();
	}

	@Override
	protected void throwValidationException() {
		super.throwValidationException();
		Assert.isTrue(limit >= 1, "同時受付数上限値は1以上でなければなりません");
	}

	@Override
	public void start() {
		this.taskSemaphore = new Semaphore(limit, true);
		super.start();
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see oldtricks.executor.Executor#submit(java.lang.Runnable)
	 */
	@Override
	public Future<?> submit(final Runnable task) throws TooManyTaskException {
		if (taskSemaphore.tryAcquire()) {
			try {
				return getExecutor().submit(new Runnable() {

					@Override
					public void run() {
						try {
							createWrappedTask(task).run();
						} finally {
							taskSemaphore.release();
						}
					}
				});
			} catch (RuntimeException e) {
				taskSemaphore.release();
				throw e;
			}
		} else {
			throw new TooManyTaskException("over the task limit. limit is [" + limit + "]");
		}
	}

	public <V> Future<V> submit(final Callable<V> task) throws TooManyTaskException {
		if (taskSemaphore.tryAcquire()) {
			try {
				return getExecutor().submit(new Callable<V>() {

					@Override
					public V call() throws Exception {
						try {
							return task.call();
						} finally {
							taskSemaphore.release();
						}
					}
				});
			} catch (RuntimeException e) {
				taskSemaphore.release();
				throw e;
			}
		} else {
			throw new TooManyTaskException("over the task limit. limit is [" + limit + "]");
		}
	}

	@Override
	public void execute(Runnable command) {
		try {
			submit(command);
		} catch (TooManyTaskException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * タスクをラッピングするタスクを返却します。
	 *
	 * @param task
	 * @return
	 */
	protected Runnable createWrappedTask(Runnable task) {
		return task;
	}

	/**
	 * 同時受付数上限値を取得します。
	 *
	 * @return 同時受付数上限値
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * 同時受付数上限値を設定します。
	 *
	 * @param limit
	 *            同時受付数上限値
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "DefaultExecutor [limit=" + limit + ", Name=" + getName() + ", ThreadsCore=" + getThreadsSize()
				+ ", QueueCapacity=" + getQueueCapacity() + "]";
	}

}
