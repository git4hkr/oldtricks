package oldtricks.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;

/**
 * 遅延タスクExecutorです。
 *
 *
 */
@EqualsAndHashCode(callSuper = false)
public class DelayedExecutor extends ExecutorSupport {

  private ScheduledExecutorService scheduledExecutorService;

  public DelayedExecutor() {
    super();
    this.scheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor(createThreadFactory());
    setExecutor(scheduledExecutorService);
  }

  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay,
      TimeUnit unit) {
    return scheduledExecutorService.scheduleWithFixedDelay(task, initialDelay, delay, unit);
  }

  @Deprecated
  @Override
  public Future<?> submit(Runnable task) throws TooManyTaskException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return "DelayedExecutor [scheduledExecutorService=" + scheduledExecutorService + ", Name="
        + getName() + ", ThreadsSize=" + getThreadsSize() + ", MaxThreadsSize="
        + getMaxThreadsSize() + ", QueueCapacity=" + getQueueCapacity() + "]";
  }

}
