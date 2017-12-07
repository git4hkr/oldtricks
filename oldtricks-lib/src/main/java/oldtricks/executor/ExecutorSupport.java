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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import oldtricks.util.Assert;

/**
 * スレッドプールのサポートクラスです。
 *
 *
 */
@Slf4j
@Data
public abstract class ExecutorSupport implements SimpleExecutor {

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

  public void start() {
    throwValidationException();
    if (maxThreadsSize < 0)
      maxThreadsSize = threadsSize;
    executor = new ThreadPoolExecutor(threadsSize, maxThreadsSize, 0L, TimeUnit.MILLISECONDS,
        createBlockingQueue(), createThreadFactory(), createRejectHandler());

    log.info("Starting. " + this);
  }

  protected void throwValidationException() {
    Assert.isTrue(threadsSize >= 1, "コアスレッド数は1以上でなければなりません");
    Assert.isTrue(queueCapacity >= 0, "ワーカーキュー長は0以上でなければなりません");
  }

  /**
   * {@link ThreadPoolExecutor} で実行できないタスクのハンドラです.
   * {@link RejectedExecutionException}を発生させるだけの、ハンドラを登録します.
   *
   * @return
   */
  protected RejectedExecutionHandler createRejectHandler() {
    return (Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor) -> {
      log.info("Task " + paramRunnable.toString() + " rejected from "
          + paramThreadPoolExecutor.toString());
      throw new RejectedExecutionException("Task " + paramRunnable.toString() + " rejected from "
          + paramThreadPoolExecutor.toString());
    };
  }

  /**
   * {@link ThreadPoolExecutor} 内部で利用するワーカーキューを生成します.
   *
   * @return
   */
  BlockingQueue<Runnable> createBlockingQueue() {
    if (queueCapacity > 0) {
      return new LinkedBlockingQueue<>(queueCapacity);
    } else {
      return new LinkedBlockingQueue<>();
    }
  }

  /**
   * ワーカースレッドを作成する{@link ThreadFactory}を作成します.
   *
   * @return スレッドファクトリー
   */
  ThreadFactory createThreadFactory() {
    return new ThreadFactory() {

      private AtomicInteger threadCount = new AtomicInteger();

      public Thread newThread(final Runnable r) {
        final Thread thread = new Thread(r, name + "-" + threadCount.incrementAndGet());
        thread.setDaemon(true);
        log.debug("create new thread [{}]", thread.getName());
        return thread;
      }
    };
  }

  /**
   * executorを停止します。
   *
   * @param shutdownTimeout
   *          強制終了待ち時間（秒）
   *
   * @throws InterruptedException
   *           待機中に割り込みが発生した場合
   * @see jp.lencois.executor.SimpleExecutor#shutdown()
   */
  public synchronized void shutdown(long shutdownTimeout) {

    if (executor != null) {
      log.info("stutdown start.    " + this);
      executor.shutdown();
      boolean terminated;
      try {
        terminated = executor.awaitTermination(shutdownTimeout, TimeUnit.SECONDS);
        if (!terminated) {
          log.info("shutdown force.    " + this);
          List<Runnable> nonExecs = executor.shutdownNow();
          if (!nonExecs.isEmpty()) {
            log.warn("Has uncompletion tasks. num={}, {}", nonExecs.size(), this);
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        log.info("stutdown complete. " + this);
        executor = null;
      }
    }
  }

}
