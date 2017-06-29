package oldtricks.blogic.autoconfigure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import oldtricks.blogic.autoconfigure.DataSourceRouterProperties.DataSourceProps;
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
	private Map<Object, List<String>> dataSources = new HashMap<>();
	private Map<String, DataSource> dsMap = new HashMap<>();

	@Bean
	@ConditionalOnMissingBean
	public final BLogicDataSourceRegistry blogicDataSourceRegistry() throws Exception {
		return new BLogicDataSourceRegistryImpl(dataSources, dsMap);
	}

	@Override
	public final void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected final XADataSource createXaDataSourceInstance(String className) {
		try {
			Class<?> dataSourceClass = ClassUtils.forName(className, this.classLoader);
			Object instance = BeanUtils.instantiate(dataSourceClass);
			Assert.isInstanceOf(XADataSource.class, instance);
			return (XADataSource) instance;
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to create XADataSource instance from '" + className + "'");
		}
	}

	protected final DataSource createDataSource(String uniqueResourceName, String url, Map<String, Object> poolProp)
			throws Exception {
		String className = (String) poolProp.get("driverClassName");
		Assert.state(StringUtils.hasLength(className), "No XA DataSource class name specified");
		XADataSource dataSource = createXaDataSourceInstance(className);
		MutablePropertyValues values = new MutablePropertyValues(poolProp);
		values.add("uniqueResourceName", uniqueResourceName);
		values.add("url", url);
		new RelaxedDataBinder(dataSource).bind(values);
		DataSource ds = this.wrapper.wrapDataSource(dataSource);
		new RelaxedDataBinder(ds).bind(values);
		return ds;
	}

	protected final BLogicDataSourceKey createDataSourceKey(DataSourceProps dsProp) throws Exception {
		BLogicDataSourceKey key = new BLogicDataSourceKey();
		BeanUtils.copyProperties(dsProp, key);
		return key;
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		if (prop != null) {
			for (final DataSourceProps dsProp : this.prop.getProps()) {
				BLogicDataSourceKey _key = createDataSourceKey(dsProp);
				List<String> dsList = new ArrayList<>();
				DataSource dataSource = null;
				int index = 0;
				for (String url : dsProp.getUrl()) {
					if (!dsMap.containsKey(url)) {
						index++;
						dataSource = createDataSource(_key.getUniqueResourceName() + "_" + index, url,
								this.prop.getPool());
						dsMap.put(url, dataSource);
					} else {
						dataSource = dsMap.get(url);
					}
					dsList.add(url);
				}
				this.dataSources.put(_key, dsList);
			}
		}
	}

}
