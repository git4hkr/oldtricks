package oldtricks.blogic;

import java.lang.reflect.Method;

/**
 * BLogicのメソッドフィルターです。
 *
 * @author $Author$
 *
 */
public interface BLogicFilter {
	/**
	 * ターゲットのクラスのメソッドがフィルター対象かどうかを判定します。
	 *
	 * @param method
	 *            ターゲットのクラスのメソッド
	 * @return フィルター対象の場合はtrue
	 */
	abstract public boolean accept(Method method);

	/**
	 * ターゲットのメソッド呼び出し時にフィルター処理を行います。
	 *
	 * @param target
	 *            ターゲットインスタンス
	 * @param method
	 *            メソッド
	 * @param args
	 *            メソッドの引数配列
	 * @param next
	 *            次のフィルター
	 * @return メソッドの戻り値
	 * @throws Throwable
	 */
	abstract public Object intercept(Object target, Method method, Object[] args, Next next) throws Throwable;

	public interface Next {
		public Object invoke(Object target, Method method, Object[] args) throws Throwable;
	}
}
