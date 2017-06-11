package oldtricks.blogic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 統計情報出力フィルター制御用アノテーションです。本アノテーションを付与したメソッドで処理時間の統計情報を収集します。
 *
 * @author $Author$
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BLogicStatistic {
	/**
	 * 統計情報キーを設定します。統計情報キーはJMXのBean名に利用され、キー単位で統計情報を収集します。
	 *
	 * @return 統計情報キー
	 */
	String value();

	/**
	 * 統計情報のWindowサイズを指定します。Windowサイズとは最新から直近保持する統計情報件数のことです。
	 *
	 * @return Windowサイズ
	 */
	int windowSize() default 100000;
}
