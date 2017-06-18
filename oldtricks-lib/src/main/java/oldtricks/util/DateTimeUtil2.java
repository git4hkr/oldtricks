package oldtricks.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class DateTimeUtil2 {
	/** デバッグ向け現在時刻 */
	private static ThreadLocal<LocalDateTime> NOW = new ThreadLocal<>();

	/** デバッグ向け現在時刻を設定します */
	public static void setNow(LocalDateTime now) {
		NOW.set(now);
	}

	private static LocalDateTime getNow() {
		return NOW.get();
	}

	/**
	 * 現在時刻を取得します。
	 *
	 * @return 現在時刻のLocalDateTime
	 */
	public static LocalDateTime now() {
		LocalDateTime now = getNow();
		if (now != null)
			return now;
		return LocalDateTime.now();
	}

	public static LocalDateTime parse(String val, String pattern) {
		return LocalDateTime.parse(val, DateTimeFormatter.ofPattern(pattern));
	}

	public static LocalDateTime toZonedDateTime(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	public static Date toDate(LocalDateTime dateTime) {
		ZoneId zone = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zone);
		Instant instant = zonedDateTime.toInstant();
		Date date = Date.from(instant);
		return date;
	}

	public static Timestamp toTimestamp(LocalDateTime localDateTime) {
		return Timestamp.valueOf(localDateTime);
	}
}
