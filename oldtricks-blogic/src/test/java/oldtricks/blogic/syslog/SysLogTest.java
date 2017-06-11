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
package oldtricks.blogic.syslog;

import org.junit.Test;

import oldtricks.blogic.SysLog;

public class SysLogTest {

	/**
	 * ログ出力を目視にて確認。
	 */
	@Test
	public void test() {
		// 正常系 MASGIDあり パラメータあり、エスケープ文字あり、TRACE
		SysLog.syslog("TSTM00001", new Object[] { 100 });
		// 正常系 MASGIDあり TRACE
		SysLog.syslog("TSTM00001");
		SysLog.syslog("TSTM00002", new Object[] { 101 });
		SysLog.syslog("TSTM00003", new Object[] { 102 });
		SysLog.syslog("TSTM00004", new Object[] { 103 });
		SysLog.syslog("TSTM00005", new Object[] { 104 });
		SysLog.syslog("TSTM00006", new Object[] { 105 });
		SysLog.syslog("TSTM00007", new Object[] { 106 });
		SysLog.syslog("間違いID", new Object[] { 107 });
		SysLog.syslog("TSTM0000000000", new Object[] { 108 });
		SysLog.syslog("日本語MSGID", new Object[] { 109 });
		SysLog.syslog("TSTM00010", new Object[] { 106, "文\"字列", true });
		SysLog.syslog("TSTM00011", new Object[] { "（タグ文字）" });
		SysLog.syslog("TSTM00012", new Object[] { "メッセージなし" });
		SysLog.syslog("TSTM00013", new Object[] { "レベル指定なし、メッセージなし" });
	}

}
