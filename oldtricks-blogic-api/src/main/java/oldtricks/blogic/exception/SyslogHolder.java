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

public interface SyslogHolder {
	/**
	 * msgIdを取得します。
	 *
	 * @return msgId
	 */
	String getMsgId();

	/**
	 * msgIdを設定します。
	 *
	 * @param msgId
	 *            msgId
	 */
	void setMsgId(String msgId);

	/**
	 * argsを取得します。
	 *
	 * @return args
	 */
	Object[] getArgs();

	/**
	 * argsを設定します。
	 *
	 * @param args
	 *            args
	 */
	void setArgs(Object[] args);

}
