package oldtricks.blogic.springcontext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;

import oldtricks.blogic.BLogicFilter;
import oldtricks.blogic.BLogicFilter.Next;
import oldtricks.util.Assert;

public class BLogicFilterChain implements MethodInterceptor {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(BLogicFilterChain.class);
	private List<BLogicFilter> filters;

	public BLogicFilterChain(List<BLogicFilter> filters) {
		Assert.notNull(filters, "interceptors must not be null.");
		// chainを作り易いようにリバースしておく
		this.filters = new ArrayList<BLogicFilter>(filters);
		Collections.reverse(this.filters);
	}

	public static class NextImpl implements Next {

		private BLogicFilter interceptor;
		private Next next;

		public NextImpl(BLogicFilter interceptor, Next next) {
			super();
			this.interceptor = interceptor;
			this.next = next;
		}

		public Object invoke(Object target, Method method, Object[] args) throws Throwable {
			if (interceptor.accept(method)) {
				LOG.trace("accept filter[{}] to method [{}.{}]", interceptor.getClass().getSimpleName(),
						method.getDeclaringClass(), method.getName());
				return interceptor.intercept(target, method, args, next);
			} else {
				LOG.trace("unaccept filter[{}] to method [{}.{}]", interceptor.getClass().getCanonicalName(),
						method.getDeclaringClass(), method.getName());
				return next != null ? next.invoke(target, method, args)
						: interceptor.intercept(target, method, args, next);
			}
		}
	}

	@SuppressWarnings("serial")
	public void init(Object target, ApplicationContext applicationContext) {
		// filterの初期化
		for (BLogicFilter filter : filters) {
			if (filter instanceof BLogicFilterWithLifeCycle)
				try {
					((BLogicFilterWithLifeCycle) filter).init(target, applicationContext);
				} catch (Throwable e) {
					throw new BeansException("An error occurred in BLogicFilter.init()", e) {
					};
				}
		}
	}

	@SuppressWarnings("serial")
	public void destroy() {
		// filterの初期化
		for (BLogicFilter filter : filters) {
			if (filter instanceof BLogicFilterWithLifeCycle)
				try {
					((BLogicFilterWithLifeCycle) filter).destory();
				} catch (Throwable e) {
					throw new BeansException("An error occurred in BLogicFilter.destroy()", e) {
					};
				}
		}
	}

	public boolean accept(Method method) {
		for (BLogicFilter filter : filters) {
			if (filter.accept(method))
				return true;
		}
		return false;
	}

	@Override
	public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// create filter chain
		Next next = new NextImpl(new BLogicFilter() {
			// チェーンの最後は自身のFilterとし、ターゲットを呼び出す
			@Override
			public Object intercept(Object _target, Method _method, Object[] _args, Next _next) throws Throwable {
				return proxy.invokeSuper(_target, _args);
			}

			@Override
			public boolean accept(Method method) {
				return false;
			}
		}, null);
		for (BLogicFilter interceptor : filters) {
			next = new NextImpl(interceptor, next);
		}
		// invoke filter chain
		return next.invoke(target, method, args);
	}
}
