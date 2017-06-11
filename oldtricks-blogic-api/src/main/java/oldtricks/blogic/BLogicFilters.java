package oldtricks.blogic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BLogicクラスに付与することで適用されるBlogicFilterをカスタマイズします。
 * 付与しない、或いはカスタマイズしなかった場合以下のフィルターが適用されます。
 * <li>統計情報フィルター（メソッドに{@link BLogicStatistic}を指定することで有効化する）</li>
 * <li>例外ハンドリングフィルター</li>
 * <li>宣言的トランザクションフィルター(メソッドに{@link BLogicTransaction}を指定することで有効化する)</li>
 * <li>BLogicFunctionフィルター</li>
 *
 * @author $Author$
 *
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BLogicFilters {

	/**
	 * 適用する{@link BLogicFilter}のリストを適用順で指定します。
	 *
	 * @return
	 */
	Class<? extends BLogicFilter>[] value();

	/**
	 * trueの場合はデフォルトで適用される{@link BLogicFilter}を適用せず、
	 * {@link BLogicFilters#value()}
	 * で指定したフィルタのみ適用します。falseの場合はデフォルトで適用されるフィルタの後ろに
	 * {@link BLogicFilters#value()}で指定したフィルタを適用します。<br>
	 * 注）trueを指定することは責任が伴います。
	 * デフォルトフィルタをカスタムすることによって業務ロジックフレームワークが正しく動作することは保証されなくなります
	 * 。たとえばデフォルトフィルタの適用順を変えるなどはとても危険な行為です。よく理解した上で行ってください。
	 *
	 * @return フルカスタマイズする場合はtrue
	 */
	boolean fullCustom() default false;
}
