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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldtricks.exec.Command;

public class DefaultCmdBuilder {
	protected Map<String, String> env;
	protected List<String> params = new ArrayList<String>();
	protected String cmd;
	protected File workDir = new File(".");
	protected boolean inheritIO = true;

	public static DefaultCmdBuilder create() {
		return new DefaultCmdBuilder();
	}

	public DefaultCmdBuilder addEnv(String key, String value) {
		if (env == null)
			env = new HashMap<String, String>();
		env.put(key, value);
		return this;
	}

	public DefaultCmdBuilder addParam(String param) {
		params.add(param);
		return this;
	}

	public DefaultCmdBuilder command(String cmd) {
		this.cmd = cmd;
		return this;
	}

	public DefaultCmdBuilder workDir(String workDir) {
		this.workDir = new File(workDir);
		return this;
	}

	public DefaultCmdBuilder inheritIO(boolean inheritIO) {
		this.inheritIO = inheritIO;
		return this;
	}

	protected Command createInstance() {
		return new Command();
	}

	public Command build() {
		Command command = createInstance();
		List<String> cmdline = new ArrayList<String>();
		cmdline.add(cmd);
		cmdline.addAll(params);

		command.setCmdline(cmdline.toArray(new String[cmdline.size()]));
		command.setInheritIO(inheritIO);
		if (env != null)
			command.setEnv(env);
		if (workDir != null)
			command.setWorkDir(workDir);
		return command;
	}
}
