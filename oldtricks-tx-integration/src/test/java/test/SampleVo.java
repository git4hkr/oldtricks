package test;
import java.io.Serializable;

/**
 * SampleVo.
 * 
 * @author kubota
 * @version 1.0 history Symbol Date Person Note [1] 2014/01/19 kubota Generated.
 */
public class SampleVo implements Serializable {

	public static final String TABLE = "SAMPLE";

	/**
	 * id:varchar(10) <Primary Key>
	 */
	private String id;

	/**
	 * name:varchar(45)
	 */
	private String name;

	/**
	 * cre_date:datetime(0)
	 */
	private java.sql.Timestamp creDate;

	/**
	 * upd_date:datetime(0)
	 */
	private java.sql.Timestamp updDate;

	/**
	 * Constractor
	 */
	public SampleVo() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>id</code>
	 */
	public SampleVo(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.sql.Timestamp getCreDate() {
		return this.creDate;
	}

	public void setCreDate(java.sql.Timestamp creDate) {
		this.creDate = creDate;
	}

	public java.sql.Timestamp getUpdDate() {
		return this.updDate;
	}

	public void setUpdDate(java.sql.Timestamp updDate) {
		this.updDate = updDate;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[SampleVo:");
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append(" name: ");
		buffer.append(name);
		buffer.append(" creDate: ");
		buffer.append(creDate);
		buffer.append(" updDate: ");
		buffer.append(updDate);
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((updDate == null) ? 0 : updDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleVo other = (SampleVo) obj;
		if (creDate == null) {
			if (other.creDate != null)
				return false;
		} else if (!creDate.equals(other.creDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (updDate == null) {
			if (other.updDate != null)
				return false;
		} else if (!updDate.equals(other.updDate))
			return false;
		return true;
	}

}
