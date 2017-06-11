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
package oldtricks.test.db;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class DateTimeAssertionHandler extends DefaultAssertionHandler {

	public static final String DATETIME_FORMAT1 = "yyyy/MM/dd HH:mm:ss";
	public static final String DATETIME_FORMAT2 = "yyyy/MM/dd HH:mm";

	@Override
	public void assertEquals(String expect, String actual, int row, int column) {
		DateTime expectDate;
		try {
			expectDate = parse(DATETIME_FORMAT1, expect);
		} catch (IllegalArgumentException e) {
			expectDate = parse(DATETIME_FORMAT2, expect);
		}
		DateTime actualDate;
		try {
			actualDate = parse(DATETIME_FORMAT1, actual);
		} catch (IllegalArgumentException e) {
			actualDate = parse(DATETIME_FORMAT2, actual);
		}
		if (!actualDate.equals(expectDate))
			super.fail(expect, actual, row, column);
	}

	/**
	 * 日時の文字列からDateTimeに変換する
	 * 
	 * @param format
	 *            {@link SimpleDateFormat}と同じフォーマット
	 * @param val
	 *            パースする文字列
	 * @return
	 * @throws IllegalArgumentException
	 *             formatが間違っている場合、パースできなかった場合
	 */
	public static DateTime parse(String format, String val) throws IllegalArgumentException {
		return DateTimeFormat.forPattern(format).parseDateTime(val);
	}

}
