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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import oldtricks.exec.Command;
import oldtricks.util.Assert;
import oldtricks.util.StringUtil;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JVM用のコマンドビルダーです。
 *
 * @author $Author: kubota $
 *
 */
public final class JvmCmdBuilder extends DefaultCmdBuilder {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(JvmCmdBuilder.class);
	private List<String> classpath = new ArrayList<String>();
	private List<String> vmOption = new ArrayList<String>();
	private List<String> main = new ArrayList<String>();
	private List<String> apParams = new ArrayList<String>();

	private JvmCmdBuilder() {
	}

	/**
	 *
	 * @return ビルダー
	 */
	public static JvmCmdBuilder create() {
		return new JvmCmdBuilder();
	}

	/**
	 * executable Jarを指定します。実行時のフルパスか作業Dirからの相対パスで指定します。
	 *
	 * @param jar
	 *            jarファイル名を指定します。
	 * @return ビルダー
	 */
	public JvmCmdBuilder executable(String jar) {
		main(jar, true);
		return this;
	}

	/**
	 * mainクラスを指定します。
	 *
	 * @param mainClass
	 *            mainクラスを指定します。
	 * @return ビルダー
	 */
	public JvmCmdBuilder main(String mainClass) {
		main(mainClass, false);
		return this;
	}

	/**
	 * mainクラスを指定します。
	 *
	 * @param mainClass
	 *            mainクラスを指定します。executable jarの場合はjarファイル名を指定します。
	 * @param executable
	 *            exeutaleの場合はtrue
	 *
	 * @return 自インスタンス
	 */
	public JvmCmdBuilder main(String mainClass, boolean executable) {
		if (executable) {
			Assert.isTrue(new File(mainClass).exists(), "executable jar not found.");
			main.add("-jar");
		}
		main.add(mainClass);
		return this;
	}

	/**
	 * コマンドライン文字列を生成します。
	 *
	 * @return コマンドライン文字列の配列を返却します
	 */
	public Command build() {
		this.cmd = isWindows() ? "java.exe" : "java";

		if (classpath.size() > 0) {
			params.add("-cp");
			params.add(StringUtil.join(classpath, isWindows() ? ";" : ":"));
		}
		params.addAll(vmOption);
		params.addAll(main);
		params.addAll(apParams);
		return super.build();
	}

	/**
	 * アプリケーションパラメータを追加します。
	 *
	 * @param val
	 *            アプリケーションパラメータ
	 * @return 自インスタンス
	 */
	public JvmCmdBuilder addParam(String val) {
		apParams.add(val);
		return this;
	}

	/**
	 * クラスパス文字列を追加します。
	 *
	 * @param val
	 *            クラスパス文字列
	 * @return 自インスタンス
	 */
	public JvmCmdBuilder addClasspath(String val) {
		classpath.add(val);
		return this;
	}

	/**
	 * JVM起動オプションを追加します。
	 *
	 * @param val
	 *            JVM起動オプション
	 * @return ビルダー
	 */
	public JvmCmdBuilder addOption(String val) {
		vmOption.add(val);
		return this;
	}

	/**
	 * -serverオプションを追加します.
	 *
	 * @return ビルダー
	 */
	public JvmCmdBuilder server() {
		addOption("-server");
		return this;
	}

	/**
	 * コンカレントGCを有効にします。NEW領域のパラレルGCも有効にします。
	 *
	 * @return ビルダー
	 */
	public JvmCmdBuilder useConcMarkSweepGC() {
		addOption("-XX:+UseConcMarkSweepGC"); // コンカレントGC
		addOption("-XX:+CMSParallelRemarkEnabled"); // FullGCのRemarkフェイズをマルチスレッドで実行
		addOption("-XX:+UseParNewGC"); // NEW領域のGCをマスチスレッドで実行
		return this;
	}

	/**
	 * NEW領域のサイズを指定します。-Xmnオプションを追加します。
	 *
	 * @param val
	 *            NEWサイズを指定します。例: 64m
	 * @return ビルダー
	 */
	public JvmCmdBuilder newSize(String val) {
		addOption("-Xmn" + val);
		return this;
	}

	/**
	 * HEAP領域の初期サイズを指定します。-Xmsオプションを追加します。
	 *
	 * @param val
	 *            HEAP領域の初期サイズを指定します。例: 64m
	 * @return ビルダー
	 */
	public JvmCmdBuilder startHeapSize(String val) {
		addOption("-Xms" + val);
		return this;
	}

	/**
	 * HEAP領域の最大サイズを指定します。-Xmxオプションを追加します。
	 *
	 * @param val
	 *            HEAP領域の最大サイズを指定します。例: 64m
	 * @return ビルダー
	 */
	public JvmCmdBuilder maxHeapSize(String val) {
		addOption("-Xmx" + val);
		return this;
	}

	/**
	 * Permanent領域の最大サイズを指定します。-XX:MaxPermSizeオプションを追加します。
	 *
	 * @param val
	 *            Permanent領域の最大サイズを指定します。例: 64m
	 * @return ビルダー
	 */
	public JvmCmdBuilder maxPermSize(String val) {
		addOption("-XX:MaxPermSize=" + val);
		return this;
	}

	/**
	 * スタックサイズを指定します。-Xssオプションを追加します。
	 *
	 * @param val
	 *            スタックサイズを指定します。例: 512k
	 * @return ビルダー
	 */
	public JvmCmdBuilder stackSize(String val) {
		addOption("-Xss" + val);
		return this;
	}

	/**
	 * -XX:SurvivorRatioを追加します。
	 *
	 * @param val
	 *            SurvivorRatioを指定します。SurvivorRatioはEden/Fromの比率です。例：8
	 * @return ビルダー
	 */
	public JvmCmdBuilder survivorRatio(int val) {
		addOption("-XX:SurvivorRatio" + val);
		return this;
	}

	/**
	 * JMXリモート接続を有効にします。
	 *
	 * @param jmxPort
	 *            リッスンポート
	 * @return ビルダー
	 */
	public JvmCmdBuilder withJmx(int jmxPort) {
		addOption("-Dcom.sun.management.jmxremote.port=" + jmxPort);
		addOption("-Dcom.sun.management.jmxremote.authenticate=false");
		addOption("-Dcom.sun.management.jmxremote.ssl=false");
		return this;
	}

	/**
	 * -Xverify:noneオプションを追加します。
	 * 起動高速化のためのパラメータで、クラスロード時の検証をSKIPします。検証環境では外しておき、本番時に有効にすることが望ましいです。
	 *
	 * @return ビルダー
	 */
	public JvmCmdBuilder verifyNone() {
		addOption("-Xverify:none");
		return this;
	}

	/**
	 * デバッガを有効にします。
	 *
	 * @param port
	 *            リモートから接続するデバッグポート番号
	 * @param suspend
	 *            デバッガが接続されるまでJVM起動をブロックする場合はtrue
	 * @return ビルダー
	 */
	public JvmCmdBuilder withDebug(int port, boolean suspend) {
		StringBuilder builder = new StringBuilder();
		builder.append("-agentlib:jdwp=transport=dt_socket,server=y,suspend=");
		builder.append(suspend ? "y" : "n");
		builder.append(",address=" + port);
		addOption(builder.toString());
		return this;
	}

	/**
	 * GCログ出力用パラメータを有効にします。
	 *
	 * @return ビルダー
	 */
	public JvmCmdBuilder withGcLog() {
		addOption("-verbose:gc");
		addOption("-XX:+PrintGCTimeStamps");
		addOption("-XX:+PrintGCDetails");
		addOption("-XX:+PrintClassHistogram");
		return this;
	}

	/**
	 * 指定されたディレクトリ配下で"*.jar"にマッチするファイルをクラスパスエントリーを追加します。
	 *
	 * @param jarDirirectory
	 *            クラスパスに追加するファイルが格納されているディレクトリです。
	 *            実行時のフルパスか呼び出し元プロセスの作業Dirからの相対パスで指定します。
	 * @return ビルダー
	 */
	public JvmCmdBuilder addAnyJarToClasspath(String jarDirirectory) {
		addClasspath(jarDirirectory, null);
		return this;
	}

	/**
	 * 指定されたディレクトリ配下で正規表現にマッチするファイルをクラスパスエントリーを追加します。
	 *
	 * @param jarDirirectory
	 *            クラスパスに追加するファイルが格納されているディレクトリです。
	 *            実行時のフルパスか呼び出し先プロセスの作業Dirからの相対パスで指定します。
	 * @param jarNameRegexp
	 *            {@link Pattern}で使用する文字列です。nullを指定した場合"*.jar"となります。
	 * @return ビルダー
	 */
	public JvmCmdBuilder addClasspath(String jarDirirectory, String jarNameRegexp) {
		File jarDir = new File(jarDirirectory);
		if (jarDir.isAbsolute() == false)
			jarDir = new File(workDir, jarDirirectory);
		if (!jarDir.exists())
			return this;
		if (!jarDir.isDirectory())
			return this;

		final Pattern jarPattern = Pattern.compile(jarNameRegexp == null ? ".*\\.jar$" : jarNameRegexp);
		final String[] candidates = jarDir.list();
		if (candidates != null) {
			for (String filename : candidates) {
				if (jarPattern.matcher(filename).matches()) {
					File f = new File(jarDir, filename);
					try {
						addClasspath(f.getCanonicalPath());
					} catch (IOException ignore) {
						// 無視
						LOG.trace(ignore.getMessage());
					}
				}
			}
		}
		return this;
	}

	/**
	 * Windowsかどうか判定します。
	 *
	 * @return WindowsならTrue
	 */
	static final boolean isWindows() {
		return SystemUtils.IS_OS_WINDOWS;
	}

}
