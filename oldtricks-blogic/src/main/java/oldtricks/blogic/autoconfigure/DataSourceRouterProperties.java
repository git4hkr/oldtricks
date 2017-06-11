package oldtricks.blogic.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "blogic.datasource")
public class DataSourceRouterProperties {
	private List<Map<String, Object>> props = new ArrayList<>();
}
