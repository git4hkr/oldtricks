package webmvc;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResultCode {
	NORMAL_END("0000"), /* 正常 */
	PARAM_ERR("0101"), /* パラメータエラー */
	LOGIC_ERR("0201"), /* 業務エラー */
	SYSTEM_ERR("0301") /* システムエラー */;

	private final String val;

	private ResultCode(String val) {
		this.val = val;
	}

	@JsonValue
	@Override
	public String toString() {
		return val;
	}
}
