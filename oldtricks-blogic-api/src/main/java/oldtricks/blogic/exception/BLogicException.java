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

import oldtricks.blogic.ResultCodeHolder;

public class BLogicException extends BizException implements ResultCodeHolder {

	private static final long serialVersionUID = -6381311373224425163L;
	private int resultCode = 0;

	public BLogicException(int exitCode) {
		super();
		setResultCode(exitCode);
	}

	public BLogicException(String msgId, int exitCode, Object... argArray) {
		super(msgId, argArray);
		setResultCode(exitCode);
	}

	public BLogicException(String msgId, Throwable cause, int exitCode, Object... argArray) {
		super(msgId, cause, argArray);
		setResultCode(exitCode);
	}

	public BLogicException(String msgId, Throwable cause, int exitCode) {
		super(msgId, cause);
		setResultCode(exitCode);
	}

	public BLogicException(String msgId, int exitCode) {
		super(msgId);
		setResultCode(exitCode);
	}

	/**
	 * resultCodeを取得します。
	 *
	 * @return resultCode
	 */
	public int getResultCode() {
		return resultCode;
	}

	/**
	 * resultCodeを設定します。
	 *
	 * @param resultCode
	 *            resultCode
	 */
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

}