package oldtricks.blogic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import oldtricks.blogic.exception.BLogicException;

/**
 * 業務関数の標準的な処理を行います。<br>
 * <li>呼び出し時、正常終了時、例外発生時のINFOログ出力</li>
 * <li>本アノテーションを付与したメソッドが例外をスローした場合 {@link BLogicException} でラッピングしてリスロー</li>
 *
 * @author $Author$
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BLogicFunction {
	/**
	 * ファンクション名を指定します。INFOログ出力に利用されます。
	 *
	 * @return ファンクション名
	 */
	String value();

	/**
	 * エラーログ出力時のメッセージIDを指定します。
	 *
	 * @return メッセージID
	 */
	String msgId() default "";

	/**
	 * 結果コードを指定します。コマンドラインから起動した 場合はexit_codeになります。
	 *
	 * @return 結果コード（exit_code）
	 */
	int resultCode() default 0;

	/**
	 * 詳細コードを指定します。
	 *
	 * @return 詳細コード
	 */
	int detailCode() default 0;

	/**
	 * INFOログ出力を指定します。
	 *
	 * @return ログ出力する場合はtrue
	 */
	boolean showMsg() default true;
}
