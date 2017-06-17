package oldtricks.blogic.datasource;

public class BLogicDataSourceRouter {

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

}
