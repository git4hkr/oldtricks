package oldtricks.blogic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 統計情報出力フィルター制御用アノテーションです。BLogicクラスに付与します。必須ではありません。
 *
 * @author $Author$
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BLogicStatisticConfig {
	/**
	 * 終了時に統計情報をログに出力するかどうかを指定します。
	 *
	 * @return ログ出力時はrue
	 */
	boolean dumpOnExit() default false;
}
