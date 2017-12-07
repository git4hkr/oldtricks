package oldtricks.executor;

import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCyclicExecutor implements SimpleExecutor {
  /** スレッドプール */
  private DefaultExecutor executor = new DefaultExecutor();
  /** 周期タスク */
  private Runnable task;

  public void start() {
    executor.start();
  }

  /**
   * ユーザータスクを呼び出します.<BR>
   * タスク受付上限に達していた場合は、Warnログを出力して正常応答します。
   *
   * @param userTask
   *          実行するタスク
   */
  protected final void fireTask() {
    try {
      if (task != null)
        executor.submit(task);
    } catch (Exception e) {
      log.warn(e.toString());
    }
  }

  @Override
  public Future<?> submit(Runnable task) throws TooManyTaskException {
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
   *          周期タスク
   */
  public void setTask(Runnable task) {
    this.task = task;
  }

}
