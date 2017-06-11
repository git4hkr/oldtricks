package oldtricks.blogic.springcontext.filter;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oldtricks.blogic.BLogicFilter;
import oldtricks.blogic.BLogicFunction;
import oldtricks.blogic.exception.BLogicException;
import oldtricks.tool.Stopwatch;

/**
 * {@link BatchBLogic} の {@link BLogicFunction}アノテーションが付与されたメソッドに挿入される共通処理です。
 *
 * @author $Author$
 *
 */
public class BLogicFunctionFilter implements BLogicFilter {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(BLogicFunctionFilter.class);

	private void outputLog(String msg, boolean showMsg) {
		if (showMsg)
			LOG.info(msg);
	}

	@Override
	public Object intercept(Object target, Method method, Object[] args, Next next) throws Throwable {
		LOG.trace("invoke BLogic method with BLogicFunctionFilter.[{}]", method);

		BLogicFunction function = method.getAnnotation(BLogicFunction.class);
		Stopwatch stopwatch = new Stopwatch().start();
		try {
			outputLog("------ " + function.value() + " ------- start.", function.showMsg());
			Object ret = next.invoke(target, method, args);
			outputLog("------ " + function.value() + " ------- end. [" + stopwatch + "]", function.showMsg());
			return ret;
		} catch (Exception e) {
			String funcName = function.value() == null ? method.getName() : function.value();
			outputLog("------ " + function.value() + " ------- abend. [" + stopwatch + "]", function.showMsg());
			if (e instanceof BLogicException)
				throw e;
			throw new BLogicException(function.msgId(), e, function.resultCode(), "\"" + funcName + "\" でエラーが発生しました。",
					e.getMessage());
		}
	}

	@Override
	public boolean accept(Method method) {
		try {
			return method.getAnnotation(BLogicFunction.class) != null;
		} catch (Exception e) {
			return false;
		}
	}

}
