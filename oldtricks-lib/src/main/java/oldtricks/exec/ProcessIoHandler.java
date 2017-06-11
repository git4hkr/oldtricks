package oldtricks.exec;

/**
 * 外部プロセスの入出力を処理するハンドラーです。 {@link ProcessContext} 経由で入出力を取得／操作します。
 *
 * @author $Autor$
 *
 */
public interface ProcessIoHandler {

	public abstract void handleIo(ProcessContext ctx) throws Exception;
}
