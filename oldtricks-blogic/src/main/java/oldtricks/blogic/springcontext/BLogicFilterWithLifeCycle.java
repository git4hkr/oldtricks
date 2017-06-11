package oldtricks.blogic.springcontext;

import org.springframework.context.ApplicationContext;

import oldtricks.blogic.BLogicFilter;

public interface BLogicFilterWithLifeCycle extends BLogicFilter {

	abstract public void init(Object target, ApplicationContext applicationContext) throws Throwable;

	abstract public void destory() throws Throwable;

}
