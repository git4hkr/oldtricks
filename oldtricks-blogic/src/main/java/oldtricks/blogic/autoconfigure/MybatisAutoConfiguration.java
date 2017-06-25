/**
 *    Copyright 2015-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package oldtricks.blogic.autoconfigure;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import lombok.Data;
import oldtricks.blogic.datasource.BLogicDataSourceRegistry;

/**
 * {@link EnableAutoConfiguration Auto-Configuration} for Mybatis. Contributes a
 * {@link SqlSessionFactory} and a {@link SqlSessionTemplate}.
 *
 * If {@link org.mybatis.spring.annotation.MapperScan} is used, or a
 * configuration file is specified as a property, those will be considered,
 * otherwise this auto-configuration will attempt to register mappers based on
 * the interface definitions in or under the root auto-configuration package.
 *
 * @author Eddú Meléndez
 * @author Josh Long
 * @author Kazuki Shimizu
 * @author Eduardo Macarrón
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter(BLogicXADataSourceAutoConfiguration.class)
@Import({ MybatisMapperScannerRegistrar.class })

@Data
public class MybatisAutoConfiguration {

	private MybatisProperties properties;

	private Interceptor[] interceptors;

	private ResourceLoader resourceLoader;

	private DatabaseIdProvider databaseIdProvider;

	private List<ConfigurationCustomizer> configurationCustomizers;

	public MybatisAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
			ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
			ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
		this.properties = properties;
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.databaseIdProvider = databaseIdProvider.getIfAvailable();
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
	}

	@PostConstruct
	public void checkConfigFileExists() {
		if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
			Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
			Assert.state(resource.exists(), "Cannot find config location: " + resource
					+ " (please add config file or check your Mybatis configuration)");
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public MybatisBeanFactory mybatisBeanFactory(BLogicDataSourceRegistry bLogicDataSourceRegistry) {
		return new MybatisBeanFactory();
	}

	public class MybatisBeanFactory {

		public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(dataSource);
			factory.setVfs(SpringBootVFS.class);
			if (StringUtils.hasText(properties.getConfigLocation())) {
				factory.setConfigLocation(resourceLoader.getResource(properties.getConfigLocation()));
			}
			Configuration configuration = properties.getConfiguration();
			if (configuration == null && !StringUtils.hasText(properties.getConfigLocation())) {
				configuration = new Configuration();
			}
			if (configuration != null && !CollectionUtils.isEmpty(configurationCustomizers)) {
				for (ConfigurationCustomizer customizer : configurationCustomizers) {
					customizer.customize(configuration);
				}
			}
			if (configuration != null) {
				Configuration cloneconfiguration = new Configuration();
				BeanUtils.copyProperties(configuration, cloneconfiguration);
				factory.setConfiguration(cloneconfiguration);
			}
			if (properties.getConfigurationProperties() != null) {
				factory.setConfigurationProperties(properties.getConfigurationProperties());
			}
			if (!ObjectUtils.isEmpty(interceptors)) {
				factory.setPlugins(interceptors);
			}
			if (databaseIdProvider != null) {
				factory.setDatabaseIdProvider(databaseIdProvider);
			}
			if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
				factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
			}
			if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
				factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
			}
			if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
				factory.setMapperLocations(properties.resolveMapperLocations());
			}

			return factory.getObject();
		}

		public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
			ExecutorType executorType = properties.getExecutorType();
			if (executorType != null) {
				return new SqlSessionTemplate(sqlSessionFactory, executorType);
			} else {
				return new SqlSessionTemplate(sqlSessionFactory);
			}
		}
	}
}
