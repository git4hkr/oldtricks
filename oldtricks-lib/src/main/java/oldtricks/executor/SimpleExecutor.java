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

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public interface SimpleExecutor extends ServiceLifeCycle, Executor {

	/**
	 * タスクの登録を行います。同時受付数を超過した場合、例外を発生させます。実行用の Runnable タスクを送信し、そのタスクを表す Future
	 * を返します。Future の get メソッドは、正常に完了した時点で null を返します。
	 * 
	 * @param task
	 *            タスク
	 * @return タスクの保留完了を表す Future
	 * @throws oldtricks.executor.TooManyTaskException.TooManyTaskException
	 *             同時タスク数上限に到達した
	 * @throws RejectedExecutionException
	 *             shutdown中にsubmitを呼び出した場合
	 */
	public abstract Future<?> submit(Runnable task) throws TooManyTaskException, RejectedExecutionException;

	/**
	 * プールスレッド数を取得します。
	 * 
	 * @return プールスレッド数
	 */
	public int getThreadsSize();

	/**
	 * プールスレッド数を設定します。
	 * 
	 * @param threadsSize
	 *            プールスレッド数
	 */
	public void setThreadsSize(int threadsSize);

	/**
	 * Executorの名前を設定します。
	 * 
	 * @param name
	 */
	public abstract void setName(String name);

	/**
	 * Executorの名前を取得します。
	 * 
	 * @return
	 */
	public abstract String getName();
}