package oldtricks.executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * springframeworkのbeanとして{@link PeriodecExecutor}を利用するための拡張です。
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PeriodecExecutorBean extends PeriodecExecutor {
  private long shutdownTimeout = 0;

  @PostConstruct
  public void afterPropertiesSet() {
    start();
  }

  @PreDestroy
  public void destroy() {
    shutdown(shutdownTimeout);
  }

}
