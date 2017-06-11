import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class SandBox {
	@Test
	public void cglibTest() throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(TestBean.class);
		enhancer.setInterfaces(new Class[] { Runnable.class });
		enhancer.setCallbacks(new Callback[] { new MethodInterceptor() {

			@Override
			public Object intercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject,
					MethodProxy paramMethodProxy) throws Throwable {
				return paramMethodProxy.invokeSuper(paramObject, paramArrayOfObject);
			}
		} });
		TestBean ret = (TestBean) enhancer.create();
		ret.run();

	}

	public static class TestBean implements Runnable {

		public void name() {
			System.out.println("BBBBBBBBBBBBBBB");
		}

		@Override
		public void run() {
			System.out.println("AAAAAAAAAAAAAAAAAA");
			name();
		}

	}
}
