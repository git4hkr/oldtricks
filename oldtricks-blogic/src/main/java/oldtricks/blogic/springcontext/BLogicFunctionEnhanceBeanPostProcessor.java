package oldtricks.blogic.springcontext;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import oldtricks.blogic.BLogic;
import oldtricks.blogic.BLogicFilter;
import oldtricks.blogic.BLogicFilters;
import oldtricks.blogic.datasource.BLogicDataSourceRouterFilter;
import oldtricks.blogic.springcontext.filter.BLogicFunctionFilter;
import oldtricks.blogic.springcontext.filter.BLogicTransactionFilter;
import oldtricks.blogic.springcontext.filter.ExceptionHandlerFilter;
import oldtricks.blogic.springcontext.filter.StatisticsFilter;

public class BLogicFunctionEnhanceBeanPostProcessor implements InstantiationAwareBeanPostProcessor,
		ApplicationListener<ContextClosedEvent>, ApplicationContextAware, InitializingBean {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(BLogicFunctionEnhanceBeanPostProcessor.class);
	private ApplicationContext applicationContext;
	private Set<Class<?>> createdCglibClasses = new HashSet<Class<?>>();
	private BLogicFilterChain filterChain;

	public BLogicFunctionEnhanceBeanPostProcessor() {

	}

	public BLogicFunctionEnhanceBeanPostProcessor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		LOG.debug("postProcessAfterInitialization: name={}, bean=[{}]", beanName, bean);
		if (createdCglibClasses.contains(bean.getClass())) {
			LOG.info("Already enhanced bean. name=[{}] Class=[{}]", beanName, bean.getClass());
			return bean;
		}

		if (bean.getClass().getAnnotation(BLogic.class) != null) {
			LOG.info("name=[{}] isAssignable [{}]", beanName, BLogic.class);
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(bean.getClass());
			enhancer.setInterfaces(bean.getClass().getInterfaces());
			List<BLogicFilter> filters = createBlogicFilters(bean);

			filterChain = new BLogicFilterChain(filters);
			filterChain.init(bean, applicationContext);
			enhancer.setCallbacks(new Callback[] { new SerializableNoOp(), filterChain });
			enhancer.setCallbackFilter(new CallbackFilter() {

				@Override
				public int accept(Method method) {
					if (!method.isBridge() && filterChain.accept(method)) {
						LOG.info("Enhanced with cglib proxy -> {}() method in spring bean named [{}]", method.getName(),
								beanName);
						// フィルタチェインのフィルタのうち１つでもacceptで、かつブリッジメソッドでなければinterceptする。
						return 1;
					}
					return 0;
				}
			});
			// weavingしたインスタンスを生成
			Object ret = enhancer.create();
			createdCglibClasses.add(ret.getClass());
			// 生成したインスタンスにautowired
			applicationContext.getAutowireCapableBeanFactory().configureBean(ret, beanName);
			return ret;
		}
		return bean;
	}

	/**
	 * 適用する {@link BlogicFilter}のリストを生成します。対象のBLogicに{@link BLogicFilters}
	 * が付与されていない場合はデフォルトのフィルタを適用します。 {@link BLogicFilters}が付与されており、
	 * {@link BLogicFilters#fullCustom()}がfalseの場合、デフォルトのフィルタの後ろに
	 * {@link BLogicFilters#value()}で指定されたフィルタを追加します。
	 * {@link BLogicFilters#fullCustom()}がtrueの場合は、
	 * {@link BLogicFilters#value()}で指定されたフィルタのみを追加します。
	 *
	 * @param bean
	 * @return
	 */
	protected List<BLogicFilter> createBlogicFilters(final Object bean) {
		List<BLogicFilter> filters = new ArrayList<BLogicFilter>();
		BLogicFilters bLogicFilters = bean.getClass().getAnnotation(BLogicFilters.class);
		if (bLogicFilters != null) {
			if (bLogicFilters.fullCustom()) {
				for (Class<? extends BLogicFilter> bLogicFilterClass : bLogicFilters.value()) {
					try {
						filters.add(bLogicFilterClass.newInstance());
					} catch (InstantiationException | IllegalAccessException ignore) {
					}
				}
			} else {
				filters.add(new StatisticsFilter());
				filters.add(new ExceptionHandlerFilter());
				filters.add(new BLogicTransactionFilter());
				filters.add(new BLogicDataSourceRouterFilter());
				filters.add(new BLogicFunctionFilter());
				for (Class<? extends BLogicFilter> bLogicFilterClass : bLogicFilters.value()) {
					try {
						filters.add(bLogicFilterClass.newInstance());
					} catch (InstantiationException | IllegalAccessException ignore) {
					}
				}

			}
		} else {
			filters.add(new StatisticsFilter());
			filters.add(new ExceptionHandlerFilter());
			filters.add(new BLogicTransactionFilter());
			filters.add(new BLogicDataSourceRouterFilter());
			filters.add(new BLogicFunctionFilter());
		}
		return filters;
	}

	/**
	 * Serializable replacement for CGLIB's NoOp interface. Public to allow use
	 * elsewhere in the framework.
	 */
	public static class SerializableNoOp implements NoOp, Serializable {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, final String beanName) throws BeansException {

		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
			String beanName) throws BeansException {
		return pvs;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		filterChain.destroy();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		((AbstractBeanFactory) applicationContext.getAutowireCapableBeanFactory()).getBeanPostProcessors().add(0, this);
	}

}
