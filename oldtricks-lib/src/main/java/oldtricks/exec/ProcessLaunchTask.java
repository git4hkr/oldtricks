package oldtricks.exec;

import java.io.File;
import java.util.concurrent.TimeUnit;

public interface ProcessLaunchTask extends Runnable {

	/**
	 * 外部プロセスの起動を待ちます。<br>
	 * <b>[注意!!]</b> もしタスクが実行前にキャンセルされた場合、かならずタイムアウトし、永久にfalseを返却し続けます。
	 * 
	 * @param timeout
	 *            unitで指定した粒度で待つ時間を指定します。
	 * @param unit
	 *            待ち時間の粒度を指定します。
	 * @return プロセスが起動済み、もしくは起動に失敗した場合はtrue。未起動の場合はfalse。
	 * @throws InterruptedException
	 *             待っている間にスレッドが割り込みされた場合
	 */
	public abstract boolean awaitStartProcess(long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * 子プロセスを終了します。
	 */
	public abstract void destroy();

	/**
	 * 子プロセスのexitコードを取得します。
	 * 
	 * @return 子プロセスのexitコード
	 */
	public abstract int getExitCode();

	/**
	 * 作業ディレクトリを取得します。
	 * 
	 * @return 作業ディレクトリ
	 */
	public abstract File getWorkDir();

}