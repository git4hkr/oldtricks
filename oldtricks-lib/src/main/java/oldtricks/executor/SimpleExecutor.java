package oldtricks.executor;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public interface SimpleExecutor extends ServiceLifeCycle {

  /**
   * タスクの登録を行います。<BR>
   * 同時受付数を超過した場合、例外を発生させます。実行用の Runnable タスクを送信し、そのタスクを表す Future を返します。<BR>
   * Future の get メソッドは、正常に完了した時点で null を返します。
   *
   * @param task
   *          タスク
   * @return タスクの保留完了を表す Future
   * @throws TooManyTaskException
   *           タスク数上限に到達した
   * @throws RejectedExecutionException
   *           shutdown中にsubmitを呼び出した場合
   */
  public abstract Future<?> submit(Runnable task) throws TooManyTaskException;

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
   *          プールスレッド数
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