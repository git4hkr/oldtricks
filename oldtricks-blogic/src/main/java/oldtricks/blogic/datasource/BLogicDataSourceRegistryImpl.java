package oldtricks.blogic.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class BLogicDataSourceRegistryImpl implements BLogicDataSourceRegistry {
	private Map<String, DataSource> urlDsMap = new HashMap<>();
	private Map<Object, List<String>> keyUrlMap = new HashMap<>();

	public BLogicDataSourceRegistryImpl(Map<Object, List<String>> urlMap, Map<String, DataSource> dsMap) {
		this.urlDsMap = dsMap;
		this.keyUrlMap = urlMap;
	}

	@Override
	public List<String> getUrls(BLogicDataSourceKey key) {
		return keyUrlMap.get(key);
	}

	@Override
	public DataSource getDataSource(String url) {
		return urlDsMap.get(url);
	}

	public Map<String, DataSource> getUrlDsMap() {
		return this.urlDsMap;
	}
}
