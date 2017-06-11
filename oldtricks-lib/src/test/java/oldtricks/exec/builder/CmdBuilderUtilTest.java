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
package oldtricks.exec.builder;

import java.io.FileNotFoundException;
import java.util.Properties;

import oldtricks.exec.Command;

import org.junit.Test;

public class CmdBuilderUtilTest {

	@Test
	public void test() {
		Command cmd0001;
		try {
			final String targetDir = "./target";
			@SuppressWarnings("serial")
			Properties props = new Properties() {
				{
					put("target.dir", targetDir);
					put("cmd.param", "replace command parameter");
					put("filter.inheritIO", "false");
				}
			};
			cmd0001 = CmdBuilderUtil.commandFromXml("src/test/resources/oldtricks/exec/builder/command-config.xml",
					"0001", props);

			Command cmd0002 = CmdBuilderUtil.commandFromXml("classpath:oldtricks/exec/builder/command-config.xml",
					"jvm.sample", props);
			System.out.println(cmd0001);
			System.out.println(cmd0002);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void dummyのテスト() throws Exception {
		Command duCommand = CmdBuilderUtil.commandFromXml("classpath:oldtricks/exec/builder/command-config.xml",
				"dummy.0001", null);
		System.out.println(duCommand);
	}
}
