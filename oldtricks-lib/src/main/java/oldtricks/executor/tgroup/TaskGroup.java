package oldtricks.executor.tgroup;

import java.util.List;

import oldtricks.executor.TooManyTaskException;

public abstract class TaskGroup<T> {

	/**
	 * タスクを実行します。タスクは非同期で実行されます。
	 * 
	 * @return
	 * @throws oldtricks.executor.TooManyTaskException.TooManyTaskException
	 */
	public abstract TaskGroup<T> submit() throws TooManyTaskException;

	/**
	 * タスクグループの完了を待ちます。
	 * 
	 * @param timeoutSec
	 *            ブロックされる時間（秒）
	 * @return 完了した場合はfalse、タイムアウトした場合はtrue
	 * @throws TaskFailureException
	 *             ブロック中に中断が発生した場合
	 * @throws InterruptedException
	 *             ブロック中に割り込みが発生した場合
	 */
	public abstract boolean awaitCompletion(long timeoutSec) throws TaskFailureException, InterruptedException,
			oldtricks.executor.tgroup.TaskFailureException;

	/**
	 * タスクの戻り値のリストを取得します。
	 * 
	 * @return タスクの戻り値のリスト
	 */
	public abstract List<T> getResults();

	/**
	 * タスクグループを終了し、リソースを開放します。
	 */
	public abstract void destroy();
}
