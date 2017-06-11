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

import static org.junit.Assert.*;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExecutorTest {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorTest.class);

	@Test
	public void 正常系() throws Exception {
		DefaultExecutor executor = new DefaultExecutor();
		executor.setName("test_pool");
		executor.setThreadsSize(10);
		executor.setLimit(10);
		executor.start();

		try {
			executor.submit(new GoingOnRunner("task-1", 2));
			executor.submit(new GoingOnRunner("task-2", 2));
			executor.submit(new GoingOnRunner("task-3", 2));
			executor.submit(new GoingOnRunner("task-4", 2));

		} catch (TooManyTaskException e1) {
			e1.printStackTrace();
			fail();
		}

		try {
			TimeUnit.SECONDS.sleep(3);
			executor.shutdown(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * 【試験条件】<br>
	 * <li>２スレッドで無限LOOPするタスクを４つ実行する。</li><br>
	 * <li>先行の２つのタスクを実行中にExecutorをシャットダウンする。</li><br>
	 * <br>
	 * 【期待値】<br>
	 * <li>shutdown(3)の呼び出しで3秒間は先行２つは止まらない</li><br>
	 * <li>３秒後shutdown(3)の呼び出しの中でshotdownNowが呼び出され、先行２つのタスクにinterruptが発生し終了する。</li>
	 * <br>
	 * <li>後発２つは実行されることなく終了する</li><br>
	 * <li>すべてのタスクのisDoneはfalseとなる</li><br>
	 * <li>すべてのタスクのisCancelledはfalseとなる</li><br>
	 * 
	 * @throws Exception
	 */
	@Test
	public void 正常系_スレット割り込み終了() throws Exception {
		DefaultExecutor executor = new DefaultExecutor();
		executor.setName("test_pool");
		executor.setThreadsSize(2);
		executor.setLimit(10);
		executor.start();

		Future<?> task1 = executor.submit(new GoingOnRunner("task-1"));
		Future<?> task2 = executor.submit(new GoingOnRunner("task-2"));
		Future<?> task3 = executor.submit(new GoingOnRunner("task-3"));
		Future<?> task4 = executor.submit(new GoingOnRunner("task-4"));
		executor.shutdown(3);
		assertFalse(task1.isDone());
		assertFalse(task2.isDone());
		assertFalse(task3.isDone());
		assertFalse(task4.isDone());
		assertFalse(task1.isCancelled());
		assertFalse(task2.isCancelled());
		assertFalse(task3.isCancelled());
		assertFalse(task4.isCancelled());
	}

	@Test
	public void 正常系_タスクキャンセル() throws Exception {
		DefaultExecutor executor = new DefaultExecutor();
		executor.setName("test_pool");
		executor.setThreadsSize(1);
		executor.setLimit(10);
		executor.start();

		try {
			executor.submit(new GoingOnRunner("task-1", 2));
			Future<?> future = executor.submit(new GoingOnRunner("task-2", 2));
			future.cancel(true);
			future = executor.submit(new GoingOnRunner("task-3", 2));
			future.cancel(true);
		} catch (TooManyTaskException e1) {
			e1.printStackTrace();
			fail();
		}

		executor.shutdown(10);
	}

	@Test
	public void 異常系_shutdown後のsubmit() throws Exception {
		DefaultExecutor executor = new DefaultExecutor();
		executor.setName("test_pool");
		executor.setThreadsSize(1);
		executor.setLimit(10);
		executor.start();

		try {
			executor.submit(new GoingOnRunner("task-1"));
			executor.getExecutor().shutdown();
			executor.submit(new GoingOnRunner("task-2"));
		} catch (TooManyTaskException e1) {
			e1.printStackTrace();
			fail();
		} catch (RejectedExecutionException e) {
			LOG.info("" + e);
		}

		executor.shutdown(10);
	}

	@Test
	public void 異常系_TooManyTaskException() throws Exception {
		DefaultExecutor executor = new DefaultExecutor();
		executor.setName("test_pool");
		executor.setThreadsSize(10);
		executor.setLimit(1);
		executor.start();

		try {
			executor.submit(new GoingOnRunner("task-1"));
			executor.submit(new GoingOnRunner("task-2"));
			fail();
		} catch (TooManyTaskException e1) {
			LOG.info(e1.toString());
			assertTrue(e1 instanceof TooManyTaskException);
		}

		executor.shutdown(0);
	}

	/**
	 * 割り込みされるまで走り続けるタスク
	 * 
	 * @author kubota
	 * 
	 */
	public static class GoingOnRunner implements Runnable {
		private String taskName;
		private int loopLimit;

		public GoingOnRunner(String taskName) {
			this(taskName, 100);
		}

		public GoingOnRunner(String taskName, int loopCnt) {
			super();
			this.taskName = taskName;
			this.loopLimit = loopCnt;
		}

		@Override
		public void run() {
			LOG.info(taskName + " start.");
			int loop = 0;
			while (loop <= loopLimit) {
				if (Thread.currentThread().isInterrupted()) {
					LOG.info(taskName + " break.");
					break;
				}
				LOG.info(taskName);
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					LOG.info(taskName + " thread interrupted.");
					Thread.currentThread().interrupt();
				} finally {
					loop++;
				}
			}
			LOG.info(taskName + " nomal end.");
		}
	}
}
