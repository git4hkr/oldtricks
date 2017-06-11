/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package oldtricks.blogic.exception;

/**
 * 業務例外です。<BR>
 * プログラムによって検出した準正常エラーが発生した場合にスローされます。<br>
 * パラメータエラー、フォーマットエラー等に相当します。基本的にシナリオコンポーネントによって利用されます。<br>
 * また、メッセージ出力機能と連携するため、MSG IDを保持します。
 *
 * @author $Author: kubota $
 *
 */
public class BizException extends Exception implements SyslogHolder {

	/**
	 *
	 */
	private static final long serialVersionUID = -4939307536128198089L;

	private String msgId;
	private Object[] args;

	public BizException() {
	}

	public BizException(String msgId) {
		this(msgId, "");
	}

	public BizException(String msgId, Throwable cause) {
		this(msgId, cause, "");
	}

	public BizException(String msgId, Throwable cause, Object... argArray) {
		super(cause);
		this.msgId = msgId;
		this.args = argArray;
	}

	public BizException(String msgId, Object... argArray) {
		this(msgId, null, argArray);
	}

	/**
	 * msgIdを取得します。
	 *
	 * @return msgId
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * msgIdを設定します。
	 *
	 * @param msgId
	 *            msgId
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * argsを取得します。
	 *
	 * @return args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * argsを設定します。
	 *
	 * @param args
	 *            args
	 */
	public void setArgs(Object[] args) {
		this.args = args;
	}

}
