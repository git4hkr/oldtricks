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
package oldtricks.exec;

import static org.junit.Assert.fail;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.TimeUnit;

import javax.management.remote.JMXConnector;

import oldtricks.exec.builder.CmdBuilderUtil;
import oldtricks.exec.builder.JvmCmdBuilder;
import oldtricks.io.Closeables;
import oldtricks.util.JmxUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CommandExecutorTest {
	@Autowired
	private CommandExecutor executor;

	public static class TestBean {
		private String name = "aaaaaaaaaaaa";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(CommandExecutorTest.class);

	@Test
	public void testName() throws Exception {
		Command cmd = CmdBuilderUtil.commandFromXml("command-config.xml", "0001", null);
		System.out.println(cmd);
		SubProcess process = executor.submitCmd(cmd);
		process.get();
	}

	@Test
	public void test() throws InterruptedException {
		CommandExecutor executor = new CommandExecutor();
		executor.setName("test-cmd-exe");
		executor.setLimit(10);
		executor.setThreadsSize(3);
		try {
			executor.start();
			JvmCmdBuilder jvmCmdBuilder = JvmCmdBuilder.create();
			jvmCmdBuilder.useConcMarkSweepGC();
			jvmCmdBuilder.workDir("../sandbox");
			Command cmdline = jvmCmdBuilder.server().stackSize("512k").maxHeapSize("64m").maxPermSize("64m")
					.verifyNone().withGcLog().withDebug(5000, false).withJmx(1999).addClasspath(".")
					.addClasspath("target/lib/*").main("Sample", false).build();
			System.out.println(cmdline);
			SubProcess process = executor.submitCmd(cmdline, new ProcessIoHandler() {

				@Override
				public void handleIo(ProcessContext ctx) throws Exception {
					LineIterator ite = IOUtils.lineIterator(ctx.getStdout(), "UTF-8");
					while (ite.hasNext()) {
						LOG.info(ite.nextLine());
					}
					// IOUtils.copy(process.getStderr(), System.err);

				}
			});
			while (process.awaitLaunchProcess(1, TimeUnit.SECONDS) == false) {
				LOG.info("waiting complete process...");
			}
			executor.submit(new Runnable() {

				@Override
				public void run() {
					// Get standard attribute "VmVendor"
					JMXConnector connector = null;
					try {
						connector = JmxUtil.getJMXConnector("localhost", 1999);
						GarbageCollectorMXBean gcMBean = JmxUtil.getGarbageCollectorMXBeansFromRemote(
								connector.getMBeanServerConnection(), JmxUtil.GC_NAME_DEFAULT_MARKSWEEP);
						if (gcMBean == null)
							gcMBean = JmxUtil.getGarbageCollectorMXBeansFromRemote(
									connector.getMBeanServerConnection(), JmxUtil.GC_NAME_CONCURRENT_MARKSWEEP);
						if (gcMBean == null)
							throw new IllegalStateException("obtain GCMXBean failed.");

						MemoryMXBean memMXBean = JmxUtil.getMemoryMXBeanFromRemote(connector.getMBeanServerConnection());

						while (!Thread.currentThread().isInterrupted()) {
							try {
								// ガーベッジコレクタの名称
								LOG.info(gcMBean.getName());
								// ガーベッジコレクションが実行された回数
								LOG.info("{} times", gcMBean.getCollectionCount());
								// ガーベッジコレクションに使われた累積時間
								LOG.info("{} ms", gcMBean.getCollectionTime());
								MemoryUsage usage = memMXBean.getHeapMemoryUsage();
								LOG.info("{}/{}", usage.getUsed(), usage.getMax());

								TimeUnit.SECONDS.sleep(5);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							} catch (Exception ignore) {
								LOG.error("An error occurred in invoking MXBean. " + ignore);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						Closeables.closeQuietly(connector);
					}
				}
			});
			LOG.info("exitcode={}", process.get());
			TimeUnit.SECONDS.sleep(10);
		} catch (Throwable e) {
			LOG.error("###", e);
			fail();
		} finally {
			executor.shutdown(0);
		}
	}
}
