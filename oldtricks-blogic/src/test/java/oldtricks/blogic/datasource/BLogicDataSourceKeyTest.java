package oldtricks.blogic.datasource;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BLogicDataSourceKeyTest {

	@Test
	public void test() {
		Map<Object, Object> map = new HashMap<>();
		map.put(new BLogicDataSourceKey("master", true, 0, "defaut"), "master");
		map.put(new BLogicDataSourceKey("shard", true, 0, "defaut"), "shard");
		System.out.println(map.get(new BLogicDataSourceKey("master", true, 0, "defaut")).toString());
		System.out.println(map.get(new BLogicDataSourceKey("shard", true, 0, "defaut")).toString());
	}

}
