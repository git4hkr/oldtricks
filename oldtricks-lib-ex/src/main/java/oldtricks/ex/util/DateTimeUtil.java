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
package oldtricks.ex.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public abstract class DateTimeUtil {
	/** デバッグ向け現在時刻 */
	private static ThreadLocal<Long> NOW = new ThreadLocal<Long>();

	/** デバッグ向け現在時刻を設定します */
	public static void setNow(Long now) {
		NOW.set(now);
	}

	private static Long getNow() {
		return NOW.get();
	}

	public enum DayOfWeek {
		月, 火, 水, 木, 金, 土, 日;

		public static DayOfWeek getDayOfWeek(DateTime dateTime) {
			switch (dateTime.getDayOfWeek()) {
			case 1:
				return DayOfWeek.月;
			case 2:
				return DayOfWeek.火;
			case 3:
				return DayOfWeek.水;
			case 4:
				return DayOfWeek.木;
			case 5:
				return DayOfWeek.金;
			case 6:
				return DayOfWeek.土;
			case 7:
				return DayOfWeek.日;
			}
			return null;
		}
	}

	/**
	 * 現在時刻を取得します。
	 * 
	 * @return 現在時刻のDateTime
	 */
	public static DateTime now() {
		Long now = getNow();
		if (now != null)
			return new DateTime(now);
		return DateTime.now();
	}

	/**
	 * 本日の00:00:00を取得します。
	 * 
	 * @return 本日の00:00:00のDateTime
	 */
	public static DateTime today() {
		return now().withTimeAtStartOfDay();
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
		return parse(format, val, Locale.JAPANESE);
	}

	/**
	 * 日時の文字列からDateTimeに変換する
	 * 
	 * @param format
	 *            {@link SimpleDateFormat}と同じフォーマット
	 * @param val
	 *            パースする文字列
	 * @param locale
	 *            ロケール
	 * @return
	 * @throws IllegalArgumentException
	 *             formatが間違っている場合、パースできなかった場合
	 */
	public static DateTime parse(String format, String val, Locale locale) throws IllegalArgumentException {
		return DateTimeFormat.forPattern(format).withLocale(locale).parseDateTime(val);
	}

	/**
	 * DateTimeから曜日のENUMを取得します。
	 * 
	 * @param dateTime
	 * @return 曜日のENUM
	 */
	public static DayOfWeek getDayOfWeek(DateTime dateTime) {
		return DayOfWeek.getDayOfWeek(dateTime);
	}

	/**
	 * 指定した日の週の月曜日を取得します。
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime get1stDayOfWeek(DateTime dateTime) {
		return dateTime.dayOfWeek().withMinimumValue();
	}

	/**
	 * 指定した日の週の日曜日を取得します。
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime getEndDayOfWeek(DateTime dateTime) {
		return dateTime.dayOfWeek().withMaximumValue();
	}

	/**
	 * 指定した日の月の1日を取得します。
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime get1stDayOfMonth(DateTime dateTime) {
		return dateTime.dayOfMonth().withMinimumValue();
	}

	/**
	 * 指定した日の月の末日を取得します。
	 * 
	 * @param dateTime
	 * @return
	 */
	public static DateTime getEndfDayOfMonth(DateTime dateTime) {
		return dateTime.dayOfMonth().withMaximumValue();
	}
}
