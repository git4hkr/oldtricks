package oldtricks.executor;

public interface ServiceLifeCycle {

  /**
   * executorの初期化を行います。初期化前にexecutor実装に必要なプロパティを設定してください。
   */
  public abstract void start();

  /**
   * executorを停止します。
   *
   * @param shutdownTimeout
   *          強制終了待ち時間（秒）
   *
   */
  public abstract void shutdown(long shutdownTimeout);

}