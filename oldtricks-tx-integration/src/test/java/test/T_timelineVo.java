package test;
import java.io.Serializable;

/**
 * T_timelineVo.
 * 
 * @author kubota
 * @version 1.0 history Symbol Date Person Note [1] 2013/12/22 kubota Generated.
 */
public class T_timelineVo implements Serializable {

	public static final String TABLE = "T_TIMELINE";

	/**
	 * timeline_id:varchar(11) <Primary Key>
	 */
	private String timelineId;

	/**
	 * rflct_ex_date:varchar(10)
	 */
	private String rflctExDate;

	/**
	 * content_id:varchar(10)
	 */
	private String contentId;

	/**
	 * cre_date:datetime(0)
	 */
	private java.sql.Timestamp creDate;

	/**
	 * Constractor
	 */
	public T_timelineVo() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>timelineId</code>
	 */
	public T_timelineVo(String timelineId) {
		this.timelineId = timelineId;
	}

	public String getTimelineId() {
		return this.timelineId;
	}

	public void setTimelineId(String timelineId) {
		this.timelineId = timelineId;
	}

	public String getRflctExDate() {
		return this.rflctExDate;
	}

	public void setRflctExDate(String rflctExDate) {
		this.rflctExDate = rflctExDate;
	}

	public String getContentId() {
		return this.contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public java.sql.Timestamp getCreDate() {
		return this.creDate;
	}

	public void setCreDate(java.sql.Timestamp creDate) {
		this.creDate = creDate;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[T_timelineVo:");
		buffer.append(" timelineId: ");
		buffer.append(timelineId);
		buffer.append(" rflctExDate: ");
		buffer.append(rflctExDate);
		buffer.append(" contentId: ");
		buffer.append(contentId);
		buffer.append(" creDate: ");
		buffer.append(creDate);
		buffer.append("]");
		return buffer.toString();
	}

}
