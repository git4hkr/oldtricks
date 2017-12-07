package oldtricks.executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * springframeworkのbeanとして{@link DelayedExecutor}を利用するための拡張です。
 *
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DelayedExecutorBean extends DelayedExecutor {
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
