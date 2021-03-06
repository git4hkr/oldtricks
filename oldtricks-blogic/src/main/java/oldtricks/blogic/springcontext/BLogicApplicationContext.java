package oldtricks.blogic.springcontext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * {@link XmlWebApplicationContext}を拡張して
 * {@link BLogicFunctionEnhanceBeanPostProcessor}を自動で追加する。
 *
 * @author $Author$
 *
 */
public class BLogicApplicationContext extends GenericXmlApplicationContext {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(BLogicApplicationContext.class);

	public BLogicApplicationContext() {
		this(null);
		LOG.info("create BLogicApplicationContext.");
	}

	public BLogicApplicationContext(String configlocation) {
		super(configlocation);
	}

	/**
	 * Instantiate and invoke all registered BeanPostProcessor beans, respecting
	 * explicit order if given.
	 * <p>
	 * Must be called before any instantiation of application beans.
	 */
	@Override
	protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		super.registerBeanPostProcessors(beanFactory);
		BLogicFunctionEnhanceBeanPostProcessor beanPostProcessor = new BLogicFunctionEnhanceBeanPostProcessor(this);
		((AbstractBeanFactory) beanFactory).getBeanPostProcessors().add(0, beanPostProcessor);
		addApplicationListener(beanPostProcessor);
	}
}