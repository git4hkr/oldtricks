package oldtricks.blogic.autoconfigure;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import oldtricks.blogic.autoconfigure.MybatisAutoConfiguration.MybatisBeanFactory;
import oldtricks.blogic.datasource.BLogicDataSourceRegistry;
import oldtricks.blogic.datasource.BLogicDataSourceRouter;
import oldtricks.blogic.mybatis.CustomClassPathMapperScanner;
import oldtricks.util.Assert;
import oldtricks.util.ClassUtil;

@Slf4j
public class MybatisMapperScannerRegistrar
		implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

	private BeanFactory beanFactory;

	private ResourceLoader resourceLoader;

	/**
	 * SpringBootのベースパッケージ配下をスキャンして
	 * {@link Mapper}アノテーションが付与されているインタフェースの実装Bean定義を追加します。<BR>
	 * 実装BeanはMapperのルーティングを行います。{@link BeanFactory}経由で生成します。
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		log.debug("Searching for mappers annotated with @Mapper");

		CustomClassPathMapperScanner scanner = new CustomClassPathMapperScanner();

		try {
			if (resourceLoader != null) {
				scanner.setResourceLoader(resourceLoader);
			}
			List<String> packages = AutoConfigurationPackages.get(beanFactory);
			if (log.isDebugEnabled()) {
				for (String pkg : packages) {
					log.debug("Using auto-configuration base package '{}'", pkg);
				}
			}
			Set<String> candidateClassNames = scanner.doScan(StringUtils.toStringArray(packages));
			for (String className : candidateClassNames) {
				GenericBeanDefinition beanDef = new GenericBeanDefinition();
				beanDef.setBeanClass(MapperRouterFactoryBean.class);
				beanDef.setScope(BeanDefinition.SCOPE_SINGLETON);
				beanDef.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
				MutablePropertyValues values = new MutablePropertyValues();
				values.addPropertyValue("bLogicDataSourceRegistry",
						new RuntimeBeanReference("blogicDataSourceRegistry"));
				values.addPropertyValue("mapperInterface", ClassUtil.getClass(className));
				values.addPropertyValue("mybatisBeanFactory", new RuntimeBeanReference("mybatisBeanFactory"));
				beanDef.setPropertyValues(values);
				BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDef, className);
				BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
			}
		} catch (Exception e) {
			log.error("", e);
		}

	}

	@SuppressWarnings("rawtypes")
	@Data
	public static class MapperRouterFactoryBean implements FactoryBean {
		private BLogicDataSourceRegistry bLogicDataSourceRegistry;
		private Class<?> mapperInterface;
		private MybatisBeanFactory mybatisBeanFactory;

		@SuppressWarnings("unchecked")
		@Override
		public Object getObject() throws Exception {
			Map<Object, Object> mappers = new HashMap<>();
			for (Entry<Object, DataSource> entry : bLogicDataSourceRegistry.getDataSources().entrySet()) {
				MapperFactoryBean mapperFactory = new MapperFactoryBean();
				mapperFactory.setMapperInterface(mapperInterface);
				SqlSessionFactory factory = mybatisBeanFactory.sqlSessionFactory(entry.getValue());
				mapperFactory.setSqlSessionFactory(factory);
				mapperFactory.setSqlSessionTemplate(mybatisBeanFactory.sqlSessionTemplate(factory));
				mapperFactory.afterPropertiesSet();
				Object mapper = mapperFactory.getObject();
				mappers.put(entry.getKey(), mapper);
			}
			// Mapperルーターの実体です。
			Object mapperProxy = Proxy.newProxyInstance(mapperInterface.getClassLoader(),
					new Class[] { mapperInterface }, new InvocationHandler() {
						private Map<Object, Object> _mappers = mappers;

						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							// データソースルーターのキーを使ってMapperのルーティングを行う。
							Object key = BLogicDataSourceRouter.getUniqueResourceId();
							Object target = _mappers.get(key);
							Assert.notNull(target,
									"Data source for the specified key can not be found in mapper registry. key=" + key.toString());
							return method.invoke(target, args);
						}
					});
			return mapperProxy;
		}

		@Override
		public Class getObjectType() {
			return mapperInterface;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}

	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
