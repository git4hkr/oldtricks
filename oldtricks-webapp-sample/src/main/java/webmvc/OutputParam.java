package webmvc;

import oldtricks.bean.AbstractBean;

/**
 * 標準的な出力データクラスです。
 *
 * @author $Author$
 *
 */
public class OutputParam extends AbstractBean {
	private static final long serialVersionUID = 1L;
	/** 結果コード */
	private ResultCode resultCode;
	/** 詳細情報 */
	private String detail;

	public OutputParam(ResultCode resultCode, String detail) {
		super();
		this.resultCode = resultCode;
		this.detail = detail;
	}

	/**
	 * 結果コードを取得します。
	 *
	 * @return 結果コード
	 */
	public ResultCode getResultCode() {
		return resultCode;
	}

	/**
	 * 結果コードを設定します。
	 *
	 * @param resultCode
	 *            結果コード
	 */
	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * 詳細情報を取得します。
	 *
	 * @return 詳細情報
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * 詳細情報を設定します。
	 *
	 * @param detail
	 *            詳細情報
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

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

}
