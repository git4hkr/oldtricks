package oldtricks.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 周期タスクExecutorです。
 *
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class PeriodecExecutor extends AbstractCyclicExecutor {
  private ScheduledExecutorService scheduledExecutor;
  /** 周期 */
  private long periodMsec;

  /**
   * @see jp.lencois.executor.PeriodecExecutor#start()
   */
  @Override
  public void start() {
    log.info("start. " + toString());
    super.start();
    scheduledExecutor = Executors.newScheduledThreadPool(1);
    scheduledExecutor.scheduleAtFixedRate(this::fireTask, 0L, periodMsec, TimeUnit.MILLISECONDS);
  }

  @Override
  public void shutdown(long shutdownTimeout) {
    scheduledExecutor.shutdownNow();
    super.shutdown(shutdownTimeout);
  }

  @Override
  public String toString() {
    return "PeriodecExecutor [scheduledExecutor=" + scheduledExecutor + ", periodMsec=" + periodMsec
        + ", ThreadsCore=" + getThreadsSize() + ", Name=" + getName() + ", Limit=" + getLimit()
        + "]";
  }

}
