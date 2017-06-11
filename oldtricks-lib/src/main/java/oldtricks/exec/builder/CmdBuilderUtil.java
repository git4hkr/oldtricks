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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.bind.JAXB;

import oldtricks.exec.Command;
import oldtricks.exec.DummyHandler;
import oldtricks.exec.builder.xml.AbstractCmd;
import oldtricks.exec.builder.xml.Cmd;
import oldtricks.exec.builder.xml.Cmd.Cmdline;
import oldtricks.exec.builder.xml.Configuration;
import oldtricks.exec.builder.xml.Dummy;
import oldtricks.exec.builder.xml.Entry;
import oldtricks.exec.builder.xml.Environment;
import oldtricks.exec.builder.xml.Jvm;
import oldtricks.exec.builder.xml.Jvm.Classpath;
import oldtricks.exec.builder.xml.Jvm.Classpath.AnyJar;
import oldtricks.exec.builder.xml.Jvm.JvmOption;
import oldtricks.exec.builder.xml.Jvm.Main;
import oldtricks.util.ClassUtil;
import oldtricks.util.ResourceUtils;
import oldtricks.util.StringUtil;

/**
 * XML形式のコンフィグファイルから{@link Command}クラスを生成します。
 *
 * @author $Author: kubota $
 *
 */
public class CmdBuilderUtil {
	private static final int DEFAULT_DUMMY_EXITCODE = 0;
	private static final int DEFAULT_DUMMY_SLEEPMS = 0;

	/**
	 * XML形式のコンフィグファイルから指定したIDのコマンドを取得します。
	 *
	 * @param configfile
	 *            コンフィグファイル
	 * @param id
	 *            コマンドID
	 * @param filter
	 *            置換文字列
	 * @return コマンド
	 * @throws FileNotFoundException
	 */
	public static Command commandFromXml(String configfile, String id, Properties filter) throws FileNotFoundException {
		return createCommand(getConfigFromXml(configfile), id, filter);
	}

	/**
	 * コンフィグから指定したIDのコマンドを取得します。
	 *
	 * @param conf
	 *            コンフィグ
	 * @param idコマンドID
	 * @param filter
	 *            置換文字列
	 * @return コマンド
	 */
	public static Command createCommand(Configuration conf, String id, Properties filter) {
		for (AbstractCmd abstractCmd : conf.getCmdOrJvmOrDummy()) {
			if (StringUtil.equals(abstractCmd.getId(), id)) {
				if (abstractCmd instanceof Jvm) {
					return configureJvmCmdBuilder(JvmCmdBuilder.create(), (Jvm) abstractCmd, filter).build();
				} else if (abstractCmd instanceof Dummy) {
					return configureDummyCmdBuilder(DummyCmdBuilder.create(), (Dummy) abstractCmd, filter).build();
				} else {
					return configureDefaultCmdBuilder(DefaultCmdBuilder.create(), (Cmd) abstractCmd, filter).build();
				}
			}
		}
		throw new IllegalArgumentException("id=[" + id + "] is not entry in configuration file.");
	}

	/**
	 * 入力ストリームからコンフィグを取得します。
	 *
	 * @param configfile
	 *            入力ストリーム
	 * @return コンフィグ
	 */
	public static Configuration getConfigFromXml(InputStream configfile) {
		Configuration conf = JAXB.unmarshal(configfile, Configuration.class);
		return conf;
	}

	/**
	 * コンフィグファイルからコンフィグを取得します。
	 *
	 * @param configfile
	 *            コンフィグファイル
	 * @return コンフィグ
	 * @throws FileNotFoundException
	 */
	public static Configuration getConfigFromXml(String configfile) throws FileNotFoundException {
		File file = ResourceUtils.getFile(configfile);
		return getConfigFromXml(new FileInputStream(file));
	}

	private static DummyCmdBuilder configureDummyCmdBuilder(DummyCmdBuilder builder, Dummy dummy, Properties filter) {
		DummyCmdBuilder duCmdBuilder = (DummyCmdBuilder) configureDefaultCmdBuilder(builder, dummy, filter);
		StripAndFilter _filter = new StripAndFilter(filter);
		String handlerClassName = _filter.value(dummy.getHandler());
		if (handlerClassName != null) {
			try {
				builder.addHandler((DummyHandler) ClassUtil.getClass(handlerClassName).newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new CommandBuildingException(e);
			}
		}
		duCmdBuilder.exitCode(_filter.intValue(dummy.getExitCode(), DEFAULT_DUMMY_EXITCODE));
		duCmdBuilder.sleepMs(_filter.intValue(dummy.getSleepMs(), DEFAULT_DUMMY_SLEEPMS));
		duCmdBuilder.stdoutValue(_filter.valueRaw(dummy.getStdout()));
		duCmdBuilder.stderrValue(_filter.valueRaw(dummy.getStderr()));
		return duCmdBuilder;
	}

	private static DefaultCmdBuilder configureDefaultCmdBuilder(DefaultCmdBuilder builder, Cmd cmd, Properties filter) {
		StripAndFilter _filter = new StripAndFilter(filter);
		// cmdline
		Cmdline cmdLine = cmd.getCmdline();
		if (cmdLine != null) {
			builder.command(cmdLine.getCommand());
			for (String param : cmdLine.getParam()) {
				builder.addParam(_filter.value(param));
			}
		}
		// env
		Environment _env = cmd.getEnvironment();
		if (_env != null) {
			for (Entry entry : _env.getEntry()) {
				builder.addEnv(_filter.value(entry.getKey()), _filter.value(entry.getValue()));
			}
		}
		if (cmd.getWorkdir() != null) {
			builder.workDir(_filter.value(cmd.getWorkdir()));
		}
		if (cmd.isInheritIO() != null) {
			builder.inheritIO(cmd.isInheritIO());
		}
		return builder;
	}

	private static JvmCmdBuilder configureJvmCmdBuilder(JvmCmdBuilder builder, Jvm jvm, Properties filter) {
		StripAndFilter _filter = new StripAndFilter(filter);
		// main
		Main _main = jvm.getMain();
		if (_main != null) {
			if (_main.getClazz() != null) {
				builder.main(_main.getClazz());
				for (String param : _main.getParam()) {
					builder.addParam(_filter.value(param));
				}
			} else if (_main.getExecutableJar() != null) {
				builder.main(_main.getExecutableJar(), true);
				for (String param : _main.getParam()) {
					builder.addParam(_filter.value(param));
				}
			}
		}
		if (jvm.getWorkdir() == null) {
			builder.workDir(".");
		} else {
			builder.workDir(_filter.value(jvm.getWorkdir()));
		}
		if (jvm.isInheritIO() != null) {
			builder.inheritIO(jvm.isInheritIO());
		}
		// classpath
		Classpath _classpath = jvm.getClasspath();
		if (_classpath != null) {
			for (AnyJar anyJar : _classpath.getAnyJar()) {
				builder.addAnyJarToClasspath(_filter.value(anyJar.getJarDirectory()));
			}
			for (String value : _classpath.getValue()) {
				builder.addClasspath(_filter.value(value));
			}
		}
		// env
		Environment _env = jvm.getEnvironment();
		if (_env != null) {
			for (Entry entry : _env.getEntry()) {
				builder.addEnv(_filter.value(entry.getKey()), _filter.value(entry.getValue()));
			}
		}
		// jvm option
		JvmOption options = jvm.getJvmOption();
		if (options != null) {
			if (options.getMaxHeapSize() != null)
				builder.maxHeapSize(_filter.value(options.getMaxHeapSize().getValue()));
			if (options.getMaxPermSize() != null)
				builder.maxPermSize(_filter.value(options.getMaxPermSize().getValue()));
			if (options.getNewSize() != null)
				builder.newSize(_filter.value(options.getNewSize().getValue()));
			if (options.getServer() != null)
				builder.server();
			if (options.getStackSize() != null)
				builder.stackSize(_filter.value(options.getStackSize().getValue()));
			if (options.getStartHeapSize() != null)
				builder.startHeapSize(_filter.value(options.getStartHeapSize().getValue()));
			if (options.getSurvivorRatio() != null)
				builder.survivorRatio(_filter.intValue(options.getSurvivorRatio().getValue(), 8));
			if (options.getUseConcMarkSweepGC() != null)
				builder.useConcMarkSweepGC();
			if (options.getVerifyNone() != null)
				builder.verifyNone();
			if (options.getWithDebug() != null)
				builder.withDebug(_filter.intValue(options.getWithDebug().getPort(), 0),
						_filter.value(options.getWithDebug().getSuspend()).equals("y"));
			if (options.getWithGcLog() != null)
				builder.withGcLog();
			if (options.getWithJmx() != null)
				builder.withJmx(_filter.intValue(options.getWithJmx().getPort(), 1099));
			if (options.getCustom() != null)
				for (String option : options.getCustom().getValue()) {
					builder.addOption(_filter.value(option));
				}
		}
		return builder;
	}

	static class StripAndFilter {
		private Properties filter;

		public StripAndFilter(Properties filter) {
			super();
			this.filter = filter;
		}

		/**
		 * 入力された文字列に置換対象があれば置換します。stripも行います。
		 *
		 * @param val
		 * @return 置換後文字列
		 */
		String value(String val) {
			if (val == null)
				return null;
			String str = StringUtil.strip(val);
			str = StringUtil.filter(str, filter);
			return str;
		}

		/**
		 * 入力された文字列に置換対象があれば置換します。stripしません。
		 *
		 * @param val
		 * @return 置換後文字列
		 */
		String valueRaw(String val) {
			if (val == null)
				return null;
			String str = StringUtil.filter(val, filter);
			return str;
		}

		int intValue(String val, int defaultValue) {
			String trimed = value(val);
			try {
				return Integer.parseInt(trimed);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}

		boolean booleanValue(String val, boolean defaultValue) {
			String trimed = value(val);
			try {
				return Boolean.parseBoolean(trimed);
			} catch (Exception e) {
				return defaultValue;
			}
		}
	}
}
