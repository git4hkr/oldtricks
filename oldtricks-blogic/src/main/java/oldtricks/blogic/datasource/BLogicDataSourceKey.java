package oldtricks.blogic.datasource;

import lombok.Data;
import oldtricks.blogic.BLogicDataSourceConfig;

@Data
public class BLogicDataSourceKey {
	private String type;
	private String availabilityZone;
	private int shardNo = 0;
	private boolean readReplica = true;

	public BLogicDataSourceKey() {
	}

	public BLogicDataSourceKey(String type, boolean readReplica, int shardNo, String availabilityZone) {
		super();
		setReadReplica(readReplica);
		setType(type);
		setShardNo(shardNo);
		setAvailabilityZone(availabilityZone);
	}

	public void setType(String type) {
		this.type = type == null ? BLogicDataSourceConfig.TYPE_MASTER : type.toLowerCase();
	}

	public String getUniqueResourceName() {
		return type + "[" + shardNo + "]_" + availabilityZone + "_" + readReplica;
	}
}
