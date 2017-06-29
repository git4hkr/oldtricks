package oldtricks.blogic.springcontext;

import org.springframework.context.ApplicationContext;

import oldtricks.blogic.BLogicFilter;

public interface BLogicFilterWithLifeCycle extends BLogicFilter {

	void init(Object target, ApplicationContext applicationContext) throws Throwable;

	void destory() throws Throwable;

}
