package oldtricks.util;

import java.time.ZonedDateTime;

import org.junit.Test;

public class NewDateTimeUtilTest {

	@Test
	public void test() throws InterruptedException {
		ZonedDateTime now = ZonedDateTime.now();
		System.out.println(now);
		System.out.println(DateTimeUtil2.toDate(now));
	}

}
