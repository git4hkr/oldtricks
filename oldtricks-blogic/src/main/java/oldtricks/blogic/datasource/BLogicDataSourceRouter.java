package oldtricks.blogic.datasource;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class BLogicDataSourceRouter extends AbstractRoutingDataSource implements DataSourceProxy {

	private static final ThreadLocal<Object> UNIQUE_RESOURCE_KEY = new ThreadLocal<>();

	public static Object getUniqueResourceId() {
		return UNIQUE_RESOURCE_KEY.get();
	}

	public static void setUniqueRsourceId(Object uniqueResourceId) {
		UNIQUE_RESOURCE_KEY.set(uniqueResourceId);
	}

	public static void clearUniqueResourceId() {
		UNIQUE_RESOURCE_KEY.remove();
	}

	public static boolean isAbsent() {
		return getUniqueResourceId() == null;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return getUniqueResourceId();
	}

	@Override
	public DataSource getRowDataSource() {
		return determineCurrentLookupKey() == null ? null : determineTargetDataSource();
	}

}
