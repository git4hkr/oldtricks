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
package oldtricks.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import oldtricks.executor.SimpleExecutor;
import oldtricks.tool.tail.TailfHandler;
import oldtricks.tool.tail.TailfTask;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class TailfTaskTest {

	@Value("#{executor}")
	private SimpleExecutor executor;

	@Test
	public void test() throws Exception {
		File file = new File("target/syslog.log");
		long delay = 100;
		TailfTask task = TailfTask.getInstance(file, "UTF-8", new TestHandler(), delay);
		executor.submit(task);
		for (int i = 0; i < 100; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		TimeUnit.SECONDS.sleep(10);
	}

	public static class TestHandler implements TailfHandler {
		/** ロガー */
		private static final Logger LOG = LoggerFactory.getLogger(TailfTaskTest.TestHandler.class);
		private TailfTask task;

		@Override
		public void init(TailfTask task) {
			this.task = task;
		}

		@Override
		public void fileNotFound(FileNotFoundException e) throws Exception {
			LOG.info("fileNotFound");
			throw e;
		}

		@Override
		public void fileRotated() throws Exception {
			LOG.info("fileRotated");
		}

		@Override
		public void handle(String line) throws Exception {
			LOG.info("handle {}", line);
		}

		@Override
		public void handleException(Exception ex) {
			LOG.info("handleException.", ex);
			task.stop();
		}

	}
}
