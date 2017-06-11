package oldtricks.ex.executor;

import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronExecutorTest {
	/** ロガー */
	private static final Logger LOG = LoggerFactory
			.getLogger(CronExecutorTest.class);

	@Test
	@Ignore
	public void test() throws Exception {
		CronExecutor executor = new CronExecutor();
		executor.setName("cron-task");
		executor.setLimit(10);
		executor.setThreadsSize(10);
		executor.setCronExpression("* * * * *");
		executor.setTask(new Runnable() {

			@Override
			public void run() {
				LOG.info("AAAAAAAAAAA");
			}
		});
		executor.start();
		TimeUnit.MINUTES.sleep(5);
		executor.shutdown(0);
	}

}
