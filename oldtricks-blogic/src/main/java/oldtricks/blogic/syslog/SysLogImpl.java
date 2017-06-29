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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.MessageFormatter;

import oldtricks.blogic.SysLog;
import oldtricks.blogic.exception.SyslogHolder;
import oldtricks.blogic.syslog.MsgLoader.Msg;

/**
 * システムメッセージ出力用のクラスです。以下にlogback.xmlで利用する場合のサンプルを示します。<br>
 * ロガーの名前を{@value #LOGGER_NAME}とし、paternで%mdcを用い {@value #MDC_MSGID}や
 * {@value #MDC_CALLER} を指定することでメッセージIDや呼び出し元を自由にレイアウトすることができます。
 *
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8" ?>
 * <!DOCTYPE configuration >
 * <configuration scan="false">
 * 	<appender name="SYSLOG" class="ch.qos.logback.core.ConsoleAppender">
 * 		<encoder>
 * 			&lt;pattern&gt;%d{yyyy-MM-dd HH:mm:ss.SSS z,JST} %-5level [%mdc{o.syslog.caller}]
 *                                                              [%mdc{o.syslog.msgid}] %msg%n&lt;/pattern&gt;
 *		</encoder>
 * 	</appender>
 * 	<logger name="o.syslog" level="TRACE" additivity="false">
 * 		<appender-ref ref="SYSLOG" />
 * 	</logger>
 *
 * </configuration>
 * }
 * </pre>
 *
 * 以下に出力サンプルを示します。
 *
 * <pre>
 * 2013-12-23 12:27:53.131 JST ERROR [oldtricks.msg.SysLogTest:16] [TSTM00004] 正常系 ERRORメッセージです"103".
 * </pre>
 *
 *
 *
 * @author kubota
 *
 */
public class SysLogImpl {
	private static final String MDC_CALLER = "o.syslog.caller";
	private static final String MDC_CALLER_FILE = "o.syslog.caller.file";
	private static final String MDC_CALLER_LINE = "o.syslog.caller.line";
	private static final String MDC_MSGID = "o.syslog.msgid";
	private static final Logger LOG = LoggerFactory.getLogger(SysLog.LOGGER_NAME);

	/**
	 *
	 * @see #syslog(SyslogHolder)
	 * @param msgId
	 *            メッセージID
	 * @param argArray
	 *            出力パラメータ
	 * @return 出力されるメッセージ文字列
	 */
	public static String syslog(String msgId, Object... argArray) {
		StackTraceElement element = new Exception().getStackTrace()[1];
		return syslog(msgId, element, argArray);
	}

	public static String syslog(String msgId, StackTraceElement caller, Object... argArray) {
		String callerFile = "???";
		String callerLineNo = "???";
		if (caller != null) {
			callerFile = caller.getClassName();
			callerLineNo = "" + caller.getLineNumber();
		}
		final String _caller = callerFile + ':' + callerLineNo;

		String message = null;
		Msg msg = MsgLoader.getMessage(msgId);
		if (msg != null) {
			message = MessageFormatter.arrayFormat(msg.getMsgFormat(), argArray).getMessage();
		} else {
			LOG.warn("不明なメッセージIDです. msgId=[{}], args={}, caller=[{}行目]", new Object[] { msgId, argArray, _caller });
			return null;
		}
		MDC.put(MDC_MSGID, msgId);
		MDC.put(MDC_CALLER, _caller);
		MDC.put(MDC_CALLER_FILE, callerFile);
		MDC.put(MDC_CALLER_LINE, callerLineNo);
		switch (msg.getLevel()) {
		case DEBUG:
			LOG.debug(message);
			break;
		case ERROR:
			LOG.error(message);
			break;
		case INFO:
			LOG.info(message);
			break;
		case WARN:
			LOG.warn(message);
			break;
		default:
			LOG.trace(message);
			break;
		}
		MDC.remove(MDC_MSGID);
		MDC.remove(MDC_CALLER);
		MDC.remove(MDC_CALLER_FILE);
		MDC.remove(MDC_CALLER_LINE);
		return message;
	}
}
