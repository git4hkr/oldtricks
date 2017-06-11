package webmvc.dao;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Sat Apr 04 23:44:16 JST 2015
 */
import java.io.Serializable;

/**
 * T_d3_contractVo.
 *
 * @author devuser
 * @version 1.0 history Symbol Date Person Note [1] 2015/04/04 devuser
 *          Generated.
 */
public class D3ContractDto implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE = "t_d3_contract";

	/**
	 * su_id:varchar(29) <Primary Key>
	 */
	private String su_id;

	/**
	 * contract_date:varchar(14) <Primary Key>
	 */
	private String contract_date;

	/**
	 * Constractor
	 */
	public D3ContractDto() {
	}

	/**
	 * Constractor
	 *
	 * @param <code>su_id</code>
	 * @param <code>contract_date</code>
	 */
	public D3ContractDto(String su_id, String contract_date) {
		this.su_id = su_id;
		this.contract_date = contract_date;
	}

	public String getSu_id() {
		return this.su_id;
	}

	public void setSu_id(String su_id) {
		this.su_id = su_id;
	}

	public String getContract_date() {
		return this.contract_date;
	}

	public void setContract_date(String contract_date) {
		this.contract_date = contract_date;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[T_d3_contractVo:");
		buffer.append(" su_id: ");
		buffer.append(su_id);
		buffer.append(" contract_date: ");
		buffer.append(contract_date);
		buffer.append("]");
		return buffer.toString();
	}

}
