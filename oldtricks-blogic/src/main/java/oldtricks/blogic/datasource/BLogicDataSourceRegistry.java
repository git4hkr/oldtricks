package oldtricks.blogic.datasource;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public interface BLogicDataSourceRegistry {

	List<String> getUrls(BLogicDataSourceKey key);

	DataSource getDataSource(String url);

	Map<String, DataSource> getUrlDsMap();
}
