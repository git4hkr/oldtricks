package oldtricks.exec;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface SubProcess extends Future<Integer> {
	/**
	 * サブプロセスが終了するまでスレッドをブロックします。サブプロセス終了後にexitコードを返却します。<BR>
	 * <b>【注意！！】</b>サブプロセスが大量の出力を標準出力や標準エラー出力に出力した場合、そのストリームを消費しないと
	 * バッファが空くまでサブプロセスの処理がブロックされ、永久に完了しません。本関数を呼び出す前に 標準出力{@link #getStdout()}
	 * と標準エラー出力 {@link #getStderr()} で得られるストリームを消費し終えるか、{@link #consumeStream()}
	 * を呼び出すことを強くおすすめします。
	 */
	public Integer get() throws InterruptedException, ExecutionException;

	/**
	 * サブプロセスが終了するまで指定された時間スレッドをブロックします。サブプロセス終了後にexitコードを返却します。<BR>
	 * <b>【注意！！】</b>サブプロセスの標準出力や標準エラー出力のストリームを消費しないと、
	 * 標準出力のバッファが空くまでサブプロセスの処理がブロックされ
	 * 、永久に完了しないことがあります。本関数を呼び出す前に標準出力と標準エラー出力を消費し終えるか、{@link #consumeStream()}
	 * を呼び出すことを強くおすすめします。
	 */
	public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	/**
	 * サブプロセスのexitコードを取得します。サブプロセスが終了していない場合は初期値(0)が返却されます。
	 * 
	 * @return サブプロセスのexitコード
	 */
	public abstract int getExitCode();

	/**
	 * サブプロセスの作業ディレクトリを取得します。
	 * 
	 * @return 作業ディレクトリ
	 */
	public abstract File geWorkDir();

	/**
	 * サブプロセスを強制終了します。本メソッドは想定外の事象が発生した場合に、ネイティブなAPIでプロセスを終了させるための緊急時用です。通常は
	 * {@link #cancel(boolean)} を利用してください。<br>
	 * タスクが実行される前に呼び出しても無視されます。 {@link #awaitLaunchProcess(long, TimeUnit)}
	 * でサブプロセスの開始を待ってから呼び出してください。
	 */
	@Deprecated
	public abstract void destroy();

	/**
	 * 外部プロセスの起動を待ちます。<br>
	 * タスクがキャンセル済みあるいは完了済みの場合は即座に復帰します。
	 * 
	 * @param timeout
	 *            unitで指定した粒度で待つ時間を指定します。
	 * @param unit
	 *            待ち時間の粒度を指定します。
	 * @return プロセスが起動済みか起動に失敗した場合、タスクがキャンセル済みあるいは完了済みの場合はtrue。未起動の場合はfalse。
	 * @throws InterruptedException
	 *             待っている間にスレッドが割り込みされた場合
	 */
	public boolean awaitLaunchProcess(long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * 後処理のテンプレートメソッドです。実行したプロセスの完了を待ちます。
	 * 
	 * @param timeout
	 *            完了待ちのタイムアウト時間（秒）を指定します。0を指定した場合無限に完了を待ちます。
	 * @return
	 * @throws ExecutionException
	 *             プロセス実行時に例外が発生した場合
	 * @throws InterruptedException
	 *             プロセス実行待ちのスレッドが割り込みされた場合
	 * @throws TimeoutException
	 *             プロセス実行待ちがタイムアウトした場合
	 */
	public SubProcess awaitCompletion(long timeout) throws ExecutionException, InterruptedException, TimeoutException;
}