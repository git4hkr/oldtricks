package oldtricks.blogic.datasource;

import java.util.Map;

import javax.sql.DataSource;

public class BLogicDataSourceRegistryImpl implements BLogicDataSourceRegistry {
	private Map<Object, DataSource> dataSources;

	public BLogicDataSourceRegistryImpl(Map<Object, DataSource> dataSources) {
		super();
		this.dataSources = dataSources;
	}

	@Override
	public DataSource get(BLogicDataSourceKey key) {
		return dataSources.get(key);
	}

	@Override
	public Map<Object, DataSource> getDataSources() {
		return dataSources;
	}

}
