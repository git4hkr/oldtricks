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

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import oldtricks.bean.AbstractBean;
import oldtricks.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * スレッドプールのサポートクラスです。
 *
 * @author $Author: kubota $
 *
 */
public abstract class ExecutorSupport extends AbstractBean implements SimpleExecutor {

	private static final long serialVersionUID = 8730921596137878653L;
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorSupport.class);

	/** ExecutorService */
	private ExecutorService executor;
	/** スレッド名プリフィックス */
	private String name = "Simple-Task-Runner";
	/** コアスレッド数 */
	private int threadsSize = 1;
	/** MAXスレッド数 */
	private int maxThreadsSize = -1;
	/** ワーカーキュー長 */
	private int queueCapacity = 0;
	/** 実行できなかったタスクリスト */
	private List<Runnable> nonExecs;

	public void start() {
		throwValidationException();
		if (maxThreadsSize < 0)
			maxThreadsSize = threadsSize;
		executor = new ThreadPoolExecutor(threadsSize, maxThreadsSize, 0L, TimeUnit.MILLISECONDS,
				createBlockingQueue(), createThreadFactory(), createRejectHandler());

		LOG.info("Starting. " + this);
	}

	protected void throwValidationException() {
		Assert.isTrue(threadsSize >= 1, "コアスレッド数は1以上でなければなりません");
		Assert.isTrue(queueCapacity >= 0, "ワーカーキュー長は0以上でなければなりません");
	}

	/**
	 * {@link ThreadPoolExecutor} で実行できないタスクのハンドラです。
	 * {@link RejectedExecutionException}を発生させるだけの、ハンドラを登録します。
	 *
	 * @return
	 */
	protected RejectedExecutionHandler createRejectHandler() {
		return new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor) {
				LOG.info("Task " + paramRunnable.toString() + " rejected from " + paramThreadPoolExecutor.toString());
				throw new RejectedExecutionException("Task " + paramRunnable.toString() + " rejected from "
						+ paramThreadPoolExecutor.toString());
			}
		};
	}

	/**
	 * {@link ThreadPoolExecutor} 内部で利用するワーカーキューを生成します。
	 *
	 * @return
	 */
	BlockingQueue<Runnable> createBlockingQueue() {
		if (queueCapacity > 0) {
			return new LinkedBlockingQueue<Runnable>(queueCapacity);
		} else {
			return new LinkedBlockingQueue<Runnable>();
		}
	}

	/**
	 * ワーカースレッドを作成する{@link ThreadFactory}を作成します。
	 *
	 * @return スレッドファクトリー
	 */
	ThreadFactory createThreadFactory() {
		return new ThreadFactory() {

			private AtomicInteger threadCount = new AtomicInteger();

			public Thread newThread(final Runnable r) {
				final Thread thread = new Thread(r, name + "-" + threadCount.incrementAndGet());
				thread.setDaemon(true);
				LOG.debug("create new thread [{}]", thread.getName());
				return thread;
			}
		};
	}

	/**
	 * executorを停止します。
	 *
	 * @param shutdownTimeout
	 *            強制終了待ち時間（秒）
	 *
	 * @throws InterruptedException
	 *             待機中に割り込みが発生した場合
	 * @see jp.lencois.executor.SimpleExecutor#shutdown()
	 */
	public synchronized void shutdown(long shutdownTimeout) {

		if (executor != null) {
			LOG.info("stutdown start.    " + this);
			executor.shutdown();
			boolean terminated;
			try {
				terminated = executor.awaitTermination(shutdownTimeout, TimeUnit.SECONDS);
				if (!terminated) {
					LOG.info("shutdown force.    " + this);
					nonExecs = executor.shutdownNow();
					if (nonExecs.size() > 0)
						LOG.warn("Has uncompletion tasks. num={}, {}", nonExecs.size(), this);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				LOG.info("stutdown complete. " + this);
				executor = null;
			}
		}
	}

	/**
	 * ExecutorServiceを取得します。
	 *
	 * @return ExecutorService
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * ExecutorServiceを設定します。
	 *
	 * @param executor
	 *            ExecutorService
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * スレッド名プリフィックスを取得します。
	 *
	 * @return スレッド名プリフィックス
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * スレッド名プリフィックスを設定します。
	 *
	 * @param name
	 *            スレッド名プリフィックス
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * コアスレッド数を取得します。
	 *
	 * @return コアスレッド数
	 */
	public int getThreadsSize() {
		return threadsSize;
	}

	/**
	 * コアスレッド数を設定します。
	 *
	 * @param threadsSize
	 *            コアスレッド数
	 */
	public void setThreadsSize(int threadsSize) {
		this.threadsSize = threadsSize;
	}

	/**
	 * MAXスレッド数を取得します。
	 *
	 * @return MAXスレッド数
	 */
	public int getMaxThreadsSize() {
		return maxThreadsSize;
	}

	/**
	 * MAXスレッド数を設定します。初期値はコアスレッド数です。
	 *
	 * @param maxThreadsSize
	 *            MAXスレッド数
	 */
	public void setMaxThreadsSize(int maxThreadsSize) {
		this.maxThreadsSize = maxThreadsSize;
	}

	/**
	 * ワーカーキュー長を取得します。
	 *
	 * @return ワーカーキュー長
	 */
	public int getQueueCapacity() {
		return queueCapacity;
	}

	/**
	 * ワーカーキュー長を設定します。
	 *
	 * @param queueCapacity
	 *            ワーカーキュー長
	 */
	@Deprecated
	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	/**
	 * 実行できなかったタスクリストを取得します。
	 *
	 * @return 実行できなかったタスクリスト
	 */
	public List<Runnable> getNonExecs() {
		return nonExecs;
	}

}
