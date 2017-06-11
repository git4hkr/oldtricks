package webmvc.dao;

import oldtricks.bean.AbstractBean;

@SuppressWarnings("serial")
public class DemoTestDto extends AbstractBean {

	public static final String TABLE = "DEMO_TEST";

	/**
	 * c1:varchar(32) <Primary Key>
	 */
	private String c1;

	/**
	 * c2:varchar(1024)
	 */
	private String c2;

	/**
	 * c3:int(10)
	 */
	private int c3;

	/**
	 * c4:bigint unsigned(20)
	 */
	private Object c4;

	/**
	 * c5:int(10)
	 */
	private int c5;

	/**
	 * Constractor
	 */
	public DemoTestDto() {
	}

	/**
	 * Constractor
	 *
	 * @param <code>c1</code>
	 */
	public DemoTestDto(String c1) {
		this.c1 = c1;
	}

	public String getC1() {
		return this.c1;
	}

	public void setC1(String c1) {
		this.c1 = c1;
	}

	public String getC2() {
		return this.c2;
	}

	public void setC2(String c2) {
		this.c2 = c2;
	}

	public int getC3() {
		return this.c3;
	}

	public void setC3(int c3) {
		this.c3 = c3;
	}

	public Object getC4() {
		return this.c4;
	}

	public void setC4(Object c4) {
		this.c4 = c4;
	}

	public int getC5() {
		return this.c5;
	}

	public void setC5(int c5) {
		this.c5 = c5;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Demo_testVo:");
		buffer.append(" c1: ");
		buffer.append(c1);
		buffer.append(" c2: ");
		buffer.append(c2);
		buffer.append(" c3: ");
		buffer.append(c3);
		buffer.append(" c4: ");
		buffer.append(c4);
		buffer.append(" c5: ");
		buffer.append(c5);
		buffer.append("]");
		return buffer.toString();
	}

}
