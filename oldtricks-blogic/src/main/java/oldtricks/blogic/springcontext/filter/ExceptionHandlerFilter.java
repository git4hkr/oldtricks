package oldtricks.blogic.springcontext.filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import oldtricks.blogic.springcontext.BLogicFilterWithLifeCycle;
import oldtricks.blogic.springcontext.filter.exception.ExceptionHandlerMethodResolver;
import oldtricks.blogic.springcontext.filter.exception.HandlerMethod;

public class ExceptionHandlerFilter implements BLogicFilterWithLifeCycle {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerFilter.class);
	private ExceptionHandlerMethodResolver exceptionMethodResolver;

	@Override
	public boolean accept(Method method) {
		try {
			return method.getAnnotation(RequestMapping.class) != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Object intercept(Object target, Method method, Object[] args, Next next) throws Throwable {
		LOG.trace("invoke BLogic method with exception handler.[{}]", method);
		try {
			return next.invoke(target, method, args);
		} catch (Exception e) {
			return exceptionHandle(target, e);
		} catch (Error e) {
			// Errorは素通りさせる
			throw e;
		}
	}

	private Object exceptionHandle(Object target, Exception e) throws Exception {
		Method method = exceptionMethodResolver.resolveMethod(e);
		if (method != null)
			return new MyHandlerMethod(target, method).invoke(e);
		else {
			LOG.info("No exception handler found. [" + e.getClass().getCanonicalName() + "]");
			throw e;
		}
	}

	@Override
	public void init(Object target, ApplicationContext applicationContext) throws Throwable {
		exceptionMethodResolver = new ExceptionHandlerMethodResolver(target.getClass());
	}

	@Override
	public void destory() throws Throwable {

	}

	private static class MyHandlerMethod extends HandlerMethod {

		public MyHandlerMethod(Object bean, Method method) {
			super(bean, method);
		}

		/**
		 * Invoke the handler method with the given argument values.
		 */
		private Object invoke(Object... args) throws Exception {
			ReflectionUtils.makeAccessible(this.getBridgedMethod());
			try {
				return getBridgedMethod().invoke(getBean(), args);
			} catch (IllegalArgumentException e) {
				String msg = getInvocationErrorMessage(e.getMessage(), args);
				throw new IllegalArgumentException(msg, e);
			} catch (InvocationTargetException e) {
				// Unwrap for HandlerExceptionResolvers ...
				Throwable targetException = e.getTargetException();
				if (targetException instanceof RuntimeException) {
					throw (RuntimeException) targetException;
				} else if (targetException instanceof Error) {
					throw (Error) targetException;
				} else if (targetException instanceof Exception) {
					throw (Exception) targetException;
				} else {
					String msg = getInvocationErrorMessage("Failed to invoke controller method", args);
					throw new IllegalStateException(msg, targetException);
				}
			}
		}

		private String getInvocationErrorMessage(String message, Object[] resolvedArgs) {
			StringBuilder sb = new StringBuilder(getDetailedErrorMessage(message));
			sb.append("Resolved arguments: \n");
			for (int i = 0; i < resolvedArgs.length; i++) {
				sb.append("[").append(i).append("] ");
				if (resolvedArgs[i] == null) {
					sb.append("[null] \n");
				} else {
					sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
					sb.append("[value=").append(resolvedArgs[i]).append("]\n");
				}
			}
			return sb.toString();
		}

		/**
		 * Adds HandlerMethod details such as the controller type and method
		 * signature to the given error message.
		 *
		 * @param message
		 *            error message to append the HandlerMethod details to
		 */
		protected String getDetailedErrorMessage(String message) {
			StringBuilder sb = new StringBuilder(message).append("\n");
			sb.append("HandlerMethod details: \n");
			sb.append("Controller [").append(getBeanType().getName()).append("]\n");
			sb.append("Method [").append(getBridgedMethod().toGenericString()).append("]\n");
			return sb.toString();
		}

	}
}
