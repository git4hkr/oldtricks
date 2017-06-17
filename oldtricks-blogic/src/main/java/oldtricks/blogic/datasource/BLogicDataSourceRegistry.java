package oldtricks.blogic.datasource;

import java.util.Map;

import javax.sql.DataSource;

public interface BLogicDataSourceRegistry {
	DataSource get(BLogicDataSourceKey key);

	Map<Object, DataSource> getDataSources();

}
