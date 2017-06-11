package oldtricks.exec;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CommandExecTemplateTest {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(CommandExecTemplateTest.class);
	@Autowired
	private CommandExecTemplate target;

	@Test
	public void test() {
		try {
			SubProcess process = target.execute("aaaa", null, new ProcessIoHandler() {

				@Override
				public void handleIo(ProcessContext ctx) throws Exception {
					ctx.printStdin("123456789ojhgfd");
					List<String> stdouts = ctx.readStdoutByString();
					for (String line : stdouts) {
						LOG.info("[STDOUT] " + line);
					}
					List<String> stderrs = ctx.readStderrByString();
					for (String line : stderrs) {
						LOG.warn("[STDERR] " + line);
					}
				}
			}).awaitCompletion(10000);
			LOG.info("exitcode=" + process.getExitCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
