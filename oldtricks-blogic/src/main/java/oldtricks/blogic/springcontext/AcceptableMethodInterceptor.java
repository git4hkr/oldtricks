package oldtricks.blogic.springcontext;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;

public interface AcceptableMethodInterceptor extends MethodInterceptor {
	boolean accept(Method method);
}
