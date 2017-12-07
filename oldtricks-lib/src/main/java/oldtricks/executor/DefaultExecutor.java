package oldtricks.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import oldtricks.util.Assert;

/**
 * 処理滞留を検知するための滞留数上限つきExecutorです。<BR>
 * 滞留数上限を超えたらタスクの受付時に例外をスローします。
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DefaultExecutor extends ExecutorSupport {
  private Semaphore taskSemaphore;
  /** 滞留数上限値 */
  private int limit = Integer.MAX_VALUE;

  /**
   * コンストラクタ
   *
   * @param name
   *          スレッドプール名
   * @param threads
   *          スレッド数
   * @param limit
   *          滞留上限数
   */
  public DefaultExecutor() {
    super();
  }

  @Override
  protected void throwValidationException() {
    super.throwValidationException();
    Assert.isTrue(limit >= 1, "滞留数上限値は1以上でなければなりません");
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
        return getExecutor().submit(() -> {
          try {
            createWrappedTask(task).run();
          } finally {
            taskSemaphore.release();
          }
        });
      } catch (Exception e) {
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
        return getExecutor().submit(() -> {
          try {
            return task.call();
          } finally {
            taskSemaphore.release();
          }
        });
      } catch (Exception e) {
        taskSemaphore.release();
        throw e;
      }
    } else {
      throw new TooManyTaskException("over task limit. limit is [" + limit + "]");
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

  @Override
  public String toString() {
    return "DefaultExecutor [limit=" + limit + ", Name=" + getName() + ", ThreadsCore="
        + getThreadsSize() + ", QueueCapacity=" + getQueueCapacity() + "]";
  }

}
