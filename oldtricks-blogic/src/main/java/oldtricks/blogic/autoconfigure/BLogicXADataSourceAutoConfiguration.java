package oldtricks.blogic.autoconfigure;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import oldtricks.blogic.datasource.BLogicDataSourceKey;
import oldtricks.blogic.datasource.BLogicDataSourceRegistry;
import oldtricks.blogic.datasource.BLogicDataSourceRegistryImpl;

@EnableConfigurationProperties(DataSourceRouterProperties.class)
@ConditionalOnClass({ DataSource.class, TransactionManager.class, EmbeddedDatabaseType.class })
@ConditionalOnMissingBean(DataSource.class)
public class BLogicXADataSourceAutoConfiguration implements BeanClassLoaderAware, InitializingBean {

	@Autowired
	private XADataSourceWrapper wrapper;

	@Autowired
	private DataSourceRouterProperties prop;

	private ClassLoader classLoader;
	private Map<Object, DataSource> xaDataSources = new HashMap<>();

	@Bean
	@ConditionalOnMissingBean
	public BLogicDataSourceRegistry blogicDataSourceRegistry() throws Exception {
		return new BLogicDataSourceRegistryImpl(xaDataSources);
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private XADataSource createXaDataSourceInstance(String className) {
		try {
			Class<?> dataSourceClass = ClassUtils.forName(className, this.classLoader);
			Object instance = BeanUtils.instantiate(dataSourceClass);
			Assert.isInstanceOf(XADataSource.class, instance);
			return (XADataSource) instance;
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to create XADataSource instance from '" + className + "'");
		}
	}

	DataSource createDataSource(String uniqueResourceName, Map<String, Object> prop) throws Exception {
		String className = (String) prop.get("driverClassName");
		Assert.state(StringUtils.hasLength(className), "No XA DataSource class name specified");
		XADataSource dataSource = createXaDataSourceInstance(className);
		MutablePropertyValues values = new MutablePropertyValues(prop);
		values.add("uniqueResourceName", uniqueResourceName);
		new RelaxedDataBinder(dataSource).bind(values);
		DataSource ds = this.wrapper.wrapDataSource(dataSource);
		new RelaxedDataBinder(ds).bind(values);
		return ds;
	}

	BLogicDataSourceKey createDataSourceKey(Map<String, Object> prop) throws Exception {
		MutablePropertyValues values = new MutablePropertyValues(prop);
		BLogicDataSourceKey key = new BLogicDataSourceKey();
		new RelaxedDataBinder(key).bind(values);
		return key;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (prop != null) {
			for (Map<String, Object> prop : this.prop.getProps()) {
				BLogicDataSourceKey key = createDataSourceKey(prop);
				xaDataSources.put(key, createDataSource(key.getUniqueResourceName(), prop));
			}
		}
	}

}
