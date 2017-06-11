package oldtricks.executor.tgroup;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import oldtricks.executor.TooManyTaskException;
import oldtricks.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TaskGroupExecutor {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(TaskGroupExecutor.class);
	/**
	 * タスクが例外をスローしたらタスクグループを停止します。
	 */
	public static final TaskExecutionPolicy EXIT_ON_FAILURE = new TaskExecutionPolicy() {

		@Override
		public <V> void handleTaskException(Throwable throwable, Callable<V> task) throws TaskFailureException {
			TaskFailureException ex = new TaskFailureException(throwable, task);
			throw ex;
		}

		public String toString() {
			return "EXIT_ON_FAILURE";
		}
	};
	/**
	 * タスクが例外をスローしてもタスクグループを続行します。
	 */
	public static final TaskExecutionPolicy IGNORE_ON_FAILURE = new TaskExecutionPolicy() {

		@Override
		public <V> void handleTaskException(Throwable throwable, Callable<V> task) throws TaskFailureException {
		}

		public String toString() {
			return "IGNORE_ON_FAILURE";
		}
	};

	/**
	 * 指定されたタスクリストをタスクグループとして実行します。すべてのタスクが完了するか失敗するまで完了を待ちます。
	 * タスクで例外がスローされた場合、即座にタスクグループの実行を終了します。
	 *
	 * @param tasks
	 *            グループ実行するタスクリスト。
	 * @param name
	 *            タスクグループ名。タスクグループを実行するスレッドの名称に利用される。
	 * @param threads
	 *            タスクグループを実行するスレッドの数。
	 * @param timeout
	 *            タイムアウト（秒）を指定します。
	 * @return タスクグループの結果です。
	 * @throws TaskFailureException
	 *             完了待ちのスレッドがブロック中に例外発生した場合
	 * @throws InterruptedException
	 *             完了待ちのスレッドがブロック中に割り込みされた場合
	 * @throws TimeoutException
	 *             タイムアウトが発生した場合
	 */
	public static <V> List<V> execute(List<Callable<V>> tasks, String name, int threads, long timeout)
			throws TaskFailureException, InterruptedException, TimeoutException,
			oldtricks.executor.tgroup.TaskFailureException {
		return execute(tasks, name, threads, timeout, EXIT_ON_FAILURE);
	}

	/**
	 * 指定されたタスクリストをタスクグループとして実行します。すべてのタスクが完了するか失敗するまで完了を待ちます。
	 *
	 * @param tasks
	 *            グループ実行するタスクリスト。
	 * @param name
	 *            タスクグループ名。タスクグループを実行するスレッドの名称に利用される。
	 * @param threads
	 *            タスクグループを実行するスレッドの数。
	 * @param timeout
	 *            タイムアウト（秒）を指定します。
	 * @param policy
	 *            タスク実行ポリシー。 {@link #EXIT_ON_FAILURE} か
	 *            {@link #IGNORE_ON_FAILURE} または独自で{@link TaskExecutionPolicy}
	 *            を実装したインスタンスを指定します。
	 * @return タスクグループの結果です。
	 * @throws TaskFailureException
	 *             実行中タスクで例外発生した場合
	 * @throws InterruptedException
	 *             完了待ちのスレッドがブロック中に割り込みされた場合
	 * @throws TimeoutException
	 *             タイムアウトが発生した場合
	 * @throws oldtricks.executor.tgroup.TaskFailureException
	 */
	public static <V> List<V> execute(List<Callable<V>> tasks, String name, int threads, long timeout,
			TaskExecutionPolicy policy) throws TaskFailureException, InterruptedException, TimeoutException,
			oldtricks.executor.tgroup.TaskFailureException {
		TaskGroup<V> taskGroup = null;
		try {
			taskGroup = executeAsync(tasks, name, threads, policy);
			if (taskGroup.awaitCompletion(timeout))
				throw new TimeoutException("timeout occurred in execution tasks. timeout=" + timeout + "sec");
			return taskGroup.getResults();
		} finally {
			if (taskGroup != null)
				taskGroup.destroy();
		}
	}

	/**
	 * 指定されたタスクリストをタスクグループとして実行します。タスクグループは非同期で実行され、Futureパターンで制御します。
	 * タスクで例外がスローされた場合、即座にタスクグループの実行を終了します。
	 *
	 * @param tasks
	 *            グループ実行するタスクリスト。
	 * @param name
	 *            タスクグループ名。タスクグループを実行するスレッドの名称に利用される。
	 * @param threads
	 *            タスクグループを実行するスレッドの数。
	 * @return タスクグループのFutureです。
	 */
	public static <V> TaskGroup<V> executeAsync(List<Callable<V>> tasks, String name, int threads) {
		return executeAsync(tasks, name, threads, EXIT_ON_FAILURE);
	}

	/**
	 * 指定されたタスクリストをタスクグループとして実行します。タスクグループは非同期で実行され、Futureパターンで制御します。
	 *
	 * @param tasks
	 *            グループ実行するタスクリスト。
	 * @param name
	 *            タスクグループ名。タスクグループを実行するスレッドの名称に利用される。
	 * @param threads
	 *            タスクグループを実行するスレッドの数。
	 * @param policy
	 *            タスク実行ポリシー。 {@link #EXIT_ON_FAILURE} か
	 *            {@link #IGNORE_ON_FAILURE} または独自で{@link TaskExecutionPolicy}
	 *            を実装したインスタンスを指定します。
	 * @return タスクグループのFutureです。
	 */
	public static <V> TaskGroup<V> executeAsync(List<Callable<V>> tasks, String name, int threads,
			TaskExecutionPolicy policy) {
		Assert.notNull(tasks, "'tasks' must not be null.");
		Assert.hasText(name, "'name' must not be enmpty.");
		TaskGroupImpl<V> tgf = new TaskGroupImpl<V>(tasks, name, threads, policy);
		try {
			tgf.submit();
		} catch (TooManyTaskException ignore) {
			LOG.trace(ignore.getLocalizedMessage());
		}
		return tgf;
	}
}
