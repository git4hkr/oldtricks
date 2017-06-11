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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import oldtricks.bean.AbstractBean;
import oldtricks.io.csv.CSVParser;
import oldtricks.io.csv.CSVReader;
import oldtricks.io.stream.LineConsumer;
import oldtricks.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Syslogメッセージをロードします。<br>
 * コンテキストクラスローダから {@value #BASENAMES} のファイルをロードし、メッセージ情報を保持します。このとき
 * クラスローダ内にある全ての同名ファイルからロードします。 {@value #BASENAMES}はカンマ区切りのCSVファイルで、
 * 各行は、メッセージID、ログレベル、メッセージフォーマットの3カラムで構成されます。<br>
 * ファイルはExcelで出力したCSVファイルを想定しており、文字コードはUTF-8でなければなりません。ログレベルはERROR, WARN, INFO,
 * DEBUG, TRACEのいずれかを指定します。メッセージフォーマットはslf4jに準じます。<br>
 * {@value #BASENAMES}のサンプルを以下に示します。<br>
 * <br>
 * <h3>message.csvサンプル</h3> <code>
 * <br>
 * "MSG00001","ERROR","ERRORメッセージです。info={}"<br>
 * "MSG00002","WARN","WARNメッセージです。"<br>
 * "MSG00003","INFO","INFOメッセージです。"<br><br>
 * 
 * </code>
 *
 * @author $Author: kubota $
 *
 */
public class MsgLoader {
	private static final Logger LOG = LoggerFactory.getLogger(MsgLoader.class);
	private static final String BASENAMES = "message.csv";
	private static Map<String, Msg> messages = new ConcurrentHashMap<String, MsgLoader.Msg>();

	static {
		try {
			Enumeration<URL> urls = MsgLoader.class.getClassLoader().getResources(BASENAMES);
			while (urls.hasMoreElements()) {
				final InputStream inputStream = urls.nextElement().openStream();
				CSVReader.newInstance(inputStream, "UTF-8", CSVParser.DEFAULT_SEPARATOR).forEach(
						new LineConsumer<String[]>() {
							@Override
							public void accept(String[] line, int lineNo) throws Exception {
								load(line);
							}
						}, CSVReader.CLOSE_AND_RETHROW_POLICY);
			}
		} catch (Throwable e) {
			LOG.error("fail to load message.", e);
		}
	}

	static void load(String[] msgRecord) throws IOException {
		Msg msg = new Msg(msgRecord);
		if (StringUtil.isNotEmpty(msg.msgid))
			messages.put(msg.msgid, msg);
	}

	public static Msg getMessage(String msgId) {
		Msg ret = null;
		if (msgId != null)
			ret = messages.get(msgId);
		return ret;
	}

	public static class Msg extends AbstractBean {

		private static final long serialVersionUID = -5366812295225739459L;
		private String msgid;
		private String msgFormat = "";
		private LogLevel level = LogLevel.DEBUG;

		public Msg(String[] record) {
			this.msgid = (record.length >= 1) ? record[0] : null;
			this.level = (record.length >= 2) ? LogLevel.fromValue(record[1]) : LogLevel.fromValue("");
			this.msgFormat = (record.length >= 3) ? record[2] : "";
		}

		public String getMsgid() {
			return msgid;
		}

		public void setMsgid(String msgid) {
			this.msgid = msgid;
		}

		public String getMsgFormat() {
			return msgFormat;
		}

		public void setMsgFormat(String msgFormat) {
			this.msgFormat = msgFormat;
		}

		public LogLevel getLevel() {
			return level;
		}

		public void setLevel(LogLevel level) {
			this.level = level;
		}

	}

	public enum LogLevel {
		ERROR, WARN, INFO, DEBUG, TRACE;

		public static LogLevel fromValue(String v) {
			for (LogLevel c : LogLevel.values()) {
				if (c.name().equalsIgnoreCase(v)) {
					return c;
				}
			}
			return DEBUG;
		}
	}
}
