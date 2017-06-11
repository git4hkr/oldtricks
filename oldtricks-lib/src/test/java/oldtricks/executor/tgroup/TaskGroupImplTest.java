package oldtricks.executor.tgroup;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import oldtricks.executor.TooManyTaskException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskGroupImplTest {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(TaskGroupImplTest.class);

	@Test
	public void test() {
		List<Callable<Integer>> tasks = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			final int no = i;
			tasks.add(new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					LOG.info("task start[{}]", no);
					if (no > 5) {
						try {
							TimeUnit.SECONDS.sleep(100);
						} catch (Exception e) {
							LOG.error("" + e);
							throw e;
						}
					}
					return no;
				}
			});
		}

		String taskName = "tasg-group";
		int concurrentry = 2;
		TaskGroup<Integer> target = new TaskGroupImpl<Integer>(tasks, taskName, concurrentry,
				TaskGroupExecutor.EXIT_ON_FAILURE);

		try {
			target.submit();
			target.awaitCompletion(10);
			for (Integer ret : target.getResults()) {
				LOG.info("ret={}", ret);
			}
			target.destroy();
			TimeUnit.SECONDS.sleep(5);
		} catch (TaskFailureException | InterruptedException | TooManyTaskException e) {
			e.printStackTrace();
			fail("まだ実装されていません");
		} finally {
			target.destroy();
		}
	}
}
