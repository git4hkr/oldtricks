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
 * システム例外です。<BR>
 * プログラムによって回復不可能なエラーが発生した場合にスローされます。<br>
 * DBアクセスエラー、ファイルIOエラー等に相当します。基本的にライブラリコンポーネントによって利用されます。<br>
 * また、メッセージ出力機能と連携するため、MSG IDを保持します。
 *
 * @author $Author: kubota $
 *
 */

public class SysException extends RuntimeException implements SyslogHolder {

	/**
	 *
	 */
	private static final long serialVersionUID = -8052886289943270725L;
	private String msgId;
	private Object[] args;

	public SysException() {
	}

	public SysException(String msgId) {
		this(msgId, "");
	}

	public SysException(String msgId, Throwable cause) {
		this(msgId, cause, "");
	}

	public SysException(String msgId, Throwable cause, Object... argArray) {
		super(cause);
		this.msgId = msgId;
		this.args = argArray;
	}

	public SysException(String msgId, Object... argArray) {
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
