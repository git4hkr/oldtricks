package oldtricks.exec;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import lombok.extern.slf4j.Slf4j;
import oldtricks.executor.DefaultExecutor;
import oldtricks.executor.TooManyTaskException;

/**
 * 外部コマンド（プロセス）を別スレッドで実行します。 プロセスの出力の取り扱いを別スレッドで実行することで、プロセスの何らかの異常で処理が遅延した際のタイムアウト処理を提供します。
 *
 * @author $Author: kubota $
 *
 */
@Slf4j
public class CommandExecutor extends DefaultExecutor {

  /**
   * 外部コマンドを非同期で実行します.<BR>
   * 外部コマンドは呼び出し元プロセスの環境変数、作業ディレクトリ、標準入出力、標準エラー出力を共有します。
   *
   * @param cmdline
   *          コマンドライン文字列
   * @param handler
   *          外部コマンドの入出力を処理するハンドラー
   * @return サブプロセスの保留完了を表す Future
   * @throws TooManyTaskException
   *           タスク数上限に到達した場合
   * @throws RejectedExecutionException
   *           shutdown中にsubmitを呼び出した場合
   */
  public SubProcess submitCmd(String[] cmdline, ProcessIoHandler handler)
      throws TooManyTaskException {
    final ProcessLaunchTask task = new ProcessLaunchTaskImpl(cmdline, null, null, true, handler);
    return submitCmd(task);
  }

  /**
   * 外部コマンドを非同期で実行します.<BR>
   * 外部コマンドは呼び出し元プロセスの標準入出力、標準エラー出力を共有します。
   *
   * @param cmdline
   *          実行するコマンドを指定します。
   * @param env
   *          環境変数。NULLの場合、実行元の環境変数を引き継ぎます。
   * @param workDir
   *          作業ディレクトリ。NULLの場合、実行元の作業ディレクトリを引き継ぎます。
   */
  public SubProcess submitCmd(String[] cmdline, Map<String, String> env, File workDir,
      ProcessIoHandler handler) throws TooManyTaskException {
    final ProcessLaunchTask task = new ProcessLaunchTaskImpl(cmdline, env, workDir, true, handler);
    return submitCmd(task);
  }

  /**
   * 外部コマンドを非同期で実行します.<BR>
   * 外部コマンドの標準入力、標準出力、標準エラー出力のストリームを取得したい場合は、inheritIO をfalseに指定し、{@link ProcessFutureImpl}から取得します。
   *
   * @param cmdline
   *          実行するコマンドを指定します。
   * @param env
   *          環境変数。NULLの場合、実行元の環境変数を引き継ぎます。
   * @param workDir
   *          作業ディレクトリ。NULLの場合、実行元の作業ディレクトリを引き継ぎます。
   * @param inheritIO
   *          呼び出し元プロセスの標準入出力、標準エラー出力を共有する場合はtrueを指定します。trueを指定した場合は
   *          {@link ProcessFutureImpl#get()}に記載された注意点にご留意ください。
   */
  public SubProcess submitCmd(String[] cmdline, Map<String, String> env, File workDir,
      boolean inheritIO, ProcessIoHandler handler) throws TooManyTaskException {
    final ProcessLaunchTask task = new ProcessLaunchTaskImpl(cmdline, env, workDir, inheritIO,
        handler);
    return submitCmd(task);
  }

  /**
   * デバッグ用.
   *
   * @param dummy
   *          ダミーコマンド
   * @param handler
   *
   * @return
   * @throws oldtricks.executor.TooManyTaskException.TooManyTaskException
   * @throws RejectedExecutionException
   */
  public SubProcess submitCmdForDebug(DummyCommand dummy, ProcessIoHandler handler)
      throws TooManyTaskException {
    ProcessLaunchTask task;
    try {
      task = new DummyProcessLaunchTask(dummy, handler);
      return submitCmd(task);
    } catch (IOException e) {
      throw new RejectedExecutionException(e);
    }
  }

  public SubProcess submitCmd(Command cmd) throws TooManyTaskException {
    return submitCmd(cmd, null);
  }

  public SubProcess submitCmd(Command cmd, ProcessIoHandler handler) throws TooManyTaskException {
    if (cmd instanceof DummyCommand)
      return submitCmdForDebug((DummyCommand) cmd, handler);
    else
      return submitCmd(cmd.getCmdline(), cmd.getEnv(), cmd.getWorkDir(), cmd.isInheritIO(),
          handler);
  }

  private SubProcess submitCmd(final ProcessLaunchTask task) throws TooManyTaskException {
    log.info("executing command with " + task.toString());
    Future<?> future = submit(task);
    return new ProcessFutureImpl(future, task);
  }

}
