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
package oldtricks.lock;

import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

import oldtricks.lock.InterProcLockTemplate.LockFailedException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterProcLockTemplateTest {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(InterProcLockTemplateTest.class);

	@Test
	public void test() {
		try {
			InterProcLockTemplate.process(new File("target/file.lock"), new ExclusiveCallback<Void>() {

				@Override
				public void exclusiveProcess(Void arg) throws Exception {
					LOG.info("排他区間");
					TimeUnit.SECONDS.sleep(5);
					throw new Exception();
				}
			}, null);
		} catch (LockFailedException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
