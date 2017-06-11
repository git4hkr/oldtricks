package oldtricks.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class DateTimeUtil2 {
	/** デバッグ向け現在時刻 */
	private static ThreadLocal<ZonedDateTime> NOW = new ThreadLocal<ZonedDateTime>();

	/** デバッグ向け現在時刻を設定します */
	public static void setNow(ZonedDateTime now) {
		NOW.set(now);
	}

	private static ZonedDateTime getNow() {
		return NOW.get();
	}

	/**
	 * 現在時刻を取得します。
	 *
	 * @return 現在時刻のZonedDateTime
	 */
	public static ZonedDateTime now() {
		ZonedDateTime now = getNow();
		if (now != null)
			return now;
		return ZonedDateTime.now();
	}

	public static ZonedDateTime parse(String val, String pattern) {
		return ZonedDateTime.parse(val, DateTimeFormatter.ofPattern(pattern));
	}

	public static ZonedDateTime toZonedDateTime(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	public static Date toDate(ZonedDateTime dateTime) {
		Instant instant = dateTime.toInstant();
		return Date.from(instant);
	}
}
