package oldtricks.exec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public interface ProcessContext {
	/**
	 * コマンドライン文字列を取得します。
	 *
	 * @return コマンドライン文字列
	 */
	public List<String> getCmdarray();

	/**
	 * 環境変数を取得します。
	 *
	 * @return 環境変数
	 */
	public Map<String, String> getEnv();

	/**
	 * 作業ディレクトリを取得します。
	 *
	 * @return 作業ディレクトリ
	 */
	public File getWorkDir();

	/**
	 * 親プロセスの標準入出力、標準エラー出力を共有するを取得します。
	 *
	 * @return 親プロセスの標準入出力、標準エラー出力を共有する
	 */
	public boolean isInheritIO();

	/**
	 * 標準出力と標準エラー出力のストリームを消費します。本関数の意図は{@link ProcessFutureImpl#get()}を参照ください。
	 */
	public abstract ProcessContext consumeStream();

	/**
	 * 標準出力から指定したエンコードで1行読み込みます。
	 *
	 * @param encoding
	 * @return 文字列
	 * @throws IOException
	 */
	public String readStdout(String encoding) throws IOException;

	/**
	 * 標準エラー出力から指定したエンコードで1行読み込みます。
	 *
	 * @param encoding
	 * @return 文字列
	 * @throws IOException
	 */
	public String readStderr(String encoding) throws IOException;

	/**
	 * 標準入力に改行とともに文字列を書き込みます。
	 *
	 * @param val
	 */
	public abstract void printStdin(String val);

	/**
	 * 標準出力を改行区切りでStringとして取得します。
	 *
	 * @return 標準出力のStringリスト
	 * @throws IOException
	 */
	public abstract List<String> readStdoutByString() throws IOException;

	/**
	 * 標準エラー出力を改行区切りでStringとして取得します。
	 *
	 * @return 標準出力のStringリスト
	 * @throws IOException
	 */
	public abstract List<String> readStderrByString() throws IOException;

	/**
	 * 標準出力を指定された{@link OutputStream}に出力します。
	 *
	 * @param out
	 *            OutputStream
	 * @param withClose
	 *            trueの場合は出力後にストリームをクローズします
	 * @return 出力したバイトサイズ
	 * @throws IOException
	 *             ファイルの出力でエラーが発生した場合
	 */
	public abstract int redirectStdoutToStream(OutputStream out, boolean withClose) throws IOException;

	/**
	 * 標準出力を指定された{@link Writer}に出力します。
	 *
	 * @param writer
	 *            Writer
	 * @param withClose
	 *            trueの場合は出力後にストリームをクローズします
	 * @throws IOException
	 *             ファイルの出力でエラーが発生した場合
	 */
	public abstract void redirectStdoutToStream(Writer writer, boolean withClose) throws IOException;

	/**
	 * 標準エラー出力を指定された{@link OutputStream}に出力します。
	 *
	 * @param out
	 *            OutputStream
	 * @param withClose
	 *            trueの場合は出力後にストリームをクローズします
	 * @return 出力したバイトサイズ
	 * @throws IOException
	 *             ファイルの出力でエラーが発生した場合
	 */
	public abstract int redirectStderrToStream(OutputStream out, boolean withClose) throws IOException;

	/**
	 * 標準エラー出力を指定された{@link Writer}に出力します。
	 *
	 * @param writer
	 *            Writer
	 * @param withClose
	 *            trueの場合は出力後にストリームをクローズします
	 * @throws IOException
	 *             ファイルの出力でエラーが発生した場合
	 */
	public abstract void redirectStderrToStream(Writer writer, boolean withClose) throws IOException;

	/**
	 * サブプロセスの標準入力を取得します。
	 *
	 * @return サブプロセスの標準入力を返却します。サブプロセスが未起動の場合はnullを返却します。
	 */
	public abstract OutputStream getStdin();

	/**
	 * サブプロセスの標準出力を取得します。
	 *
	 * @return サブプロセスの標準出力を返却します。サブプロセスが未起動の場合はnullを返却します。
	 */
	public abstract InputStream getStdout();

	/**
	 * サブプロセスの標準エラー出力を取得します。
	 *
	 * @return サブプロセスの標準エラー出力を返却します。サブプロセスが未起動の場合はnullを返却します。
	 */
	public abstract InputStream getStderr();
}
