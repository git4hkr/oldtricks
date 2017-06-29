package oldtricks.blogic.autoconfigure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "blogic.datasource")
public class DataSourceRouterProperties {
	private List<DataSourceProps> props = new ArrayList<>();
	private Map<String, Object> pool = new HashMap<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(exclude = { "url" })
	public static class DataSourceProps {
		private String type;
		private int shardNo;
		private String availabilityZone;
		private boolean isReadReplica;
		private List<String> url;
	}
}
