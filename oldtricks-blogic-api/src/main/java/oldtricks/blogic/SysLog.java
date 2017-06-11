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
package oldtricks.blogic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import oldtricks.blogic.exception.SyslogHolder;

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
 * }
 * <code>			&lt;pattern&gt;%d{yyyy-MM-dd HH:mm:ss.SSS z,JST} %-5level [%mdc{o.syslog.caller}] [%mdc{o.syslog.msgid}] %msg%n&lt;/pattern&gt;</code>
 * {@code
 * 		</encoder>
 * 	</appender>
 *
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
public class SysLog {
	public static final String LOGGER_NAME = "o.syslog";
	public static final String PREFIX = "SYSM";

	/**
	 * ロガー名 {@value #LOGGER_NAME} でログを出力します。ロガーのMDCを使い ロガーのpatternに用いることができます。
	 * 指定できるMDCは メッセージID：{@value #MDC_MSGID} と 呼び出し元：{@value #MDC_CALLER} です。
	 * 以下にlogback.xmlで利用する場合のサンプルを示します。<br>
	 *
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8" ?>
	 * <!DOCTYPE configuration >
	 * <configuration scan="false">
	 * 	<appender name="SYSLOG" class="ch.qos.logback.core.ConsoleAppender">
	 * 		<encoder>
	 * }
	 * <code>			&lt;pattern&gt;%d{yyyy-MM-dd HH:mm:ss.SSS z,JST} %-5level [%mdc{o.syslog.caller}] [%mdc{o.syslog.msgid}] %msg%n&lt;/pattern&gt;</code>
	 * {@code
	 * 		</encoder>
	 * 	</appender>
	 *
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
	 * @param holder
	 *            SysLogホルダー
	 * @return 出力されるメッセージ文字列
	 */
	public static String syslog(SyslogHolder holder) {
		StackTraceElement element = new Exception().getStackTrace()[1];
		return syslog(holder.getMsgId(), element, holder.getArgs());
	}

	/**
	 * @see #syslog(SyslogHolder)
	 * @param msgId
	 *            メッセージID
	 * @return 出力されるメッセージ文字列
	 */
	public static String syslog(String msgId) {
		StackTraceElement element = new Exception().getStackTrace()[1];
		return syslog(msgId, element, "");
	}

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

	static String syslog(String msgId, StackTraceElement caller, Object... argArray) {
		return invokeImpl(msgId, caller, argArray);
	}

	static String invokeImpl(String msgId, StackTraceElement caller, Object... argArray) {
		try {
			Method method = getMainMethod(SysLog.class.getClassLoader());
			return (String) method.invoke(null, new Object[] { msgId, caller, argArray });
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException
				| NoSuchMethodException ignore) {
		}
		return null;
	}

	static Class<?> getImplClass(ClassLoader classLoader) throws ClassNotFoundException {
		return classLoader.loadClass("oldtricks.blogic.syslog.SysLogImpl");
	}

	static Method getMainMethod(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
		Method m = getImplClass(classLoader).getMethod("syslog",
				new Class[] { String.class, StackTraceElement.class, Object[].class });

		int modifiers = m.getModifiers();
		if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
			if (m.getReturnType() == String.class) {
				return m;
			}
		}
		throw new NoSuchMethodException(
				"public static String syslog(String msgId, StackTraceElement caller, Object... argArray) in "
						+ getImplClass(classLoader));
	}
}
