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
package oldtricks.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import oldtricks.test.db.DatabaseTestSupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class RegressionTest {
	/** ロガー */
	protected static final Logger LOG = LoggerFactory.getLogger(RegressionTest.class);
	public static final String MDC_KEY_OUTPUT_PATH = "test.output.dir";
	public static final String MDC_KEY_NAME = "test.name";
	public static final String MDC_KEY_TEST_CLASS = "test.class";
	public static final String MDC_KEY_TEST_METHOD = "test.method";

	/** デフォルトの試験データROOTディレクトリ */
	protected static final String DEFAULT_BBTEST_INPUT_DIR = "regression";
	/** デフォルトの試験結果ROOTディレクトリ */
	protected static final String DEFAULT_BBTEST_OUTPOUT_DIR = "target/regression";
	private Map<String, DatabaseTestSupport> databaseTestSupports = new HashMap<String, DatabaseTestSupport>();
	@Rule
	public TestName testname = new TestName();
	private File inputRootDir = new File(DEFAULT_BBTEST_INPUT_DIR);
	private File outputRootDir = new File(DEFAULT_BBTEST_OUTPOUT_DIR);

	protected void addJdbcTemplate(String dbName, JdbcTemplate jdbcTemplate) {
		databaseTestSupports.put(dbName, new DatabaseTestSupport(dbName, jdbcTemplate));
	}

	protected String getTestClassName() {
		return getClass().getName();
	}

	protected String getTestMathodName() {
		return testname.getMethodName();
	}

	@Before
	public void setUp() throws Exception {
		databaseTestSupports.clear();
		prepareTest();
		for (DatabaseTestSupport databaseTestSupport : databaseTestSupports.values()) {
			final File inputDir = new File(this.inputRootDir, getTestClassName() + File.separator + getTestMathodName());
			final File outputDir = new File(this.outputRootDir, getTestClassName() + File.separator
					+ getTestMathodName());
			MDC.put(MDC_KEY_TEST_CLASS, getTestClassName());
			MDC.put(MDC_KEY_TEST_METHOD, getTestMathodName());
			MDC.put(MDC_KEY_NAME, getTestClassName() + "/" + getTestMathodName());
			MDC.put(MDC_KEY_OUTPUT_PATH, outputDir.getAbsolutePath());
			LOG.info("\n\n---------------------------------\n {} \n---------------------------------\n", getTestClassName()
					+ "/" + getTestMathodName());
			databaseTestSupport.preProcess(inputDir, outputDir);
		}
	}

	/**
	 * ユーザーに利用するDB名とJDBCTemplateを設定させるためのテンプレートメソッドです。ユーザーは本メソッド内で試験に必要な分だけ
	 * {@link #addJdbcTemplate(String, JdbcTemplate)}を呼び出し、JdbcTemplateを登録します。
	 */
	protected abstract void prepareTest();

	@After
	public void tearDown() throws Exception {
		try {
			for (DatabaseTestSupport databaseTestSupport : databaseTestSupports.values()) {
				final File inputDir = new File(this.inputRootDir, getTestClassName() + File.separator
						+ getTestMathodName());
				final File outputDir = new File(this.outputRootDir, getTestClassName() + File.separator
						+ getTestMathodName());
				databaseTestSupport.postProcess(inputDir, outputDir);
			}
		} finally {
			MDC.remove(MDC_KEY_TEST_CLASS);
			MDC.remove(MDC_KEY_TEST_METHOD);
			MDC.remove(MDC_KEY_NAME);
			MDC.remove(MDC_KEY_OUTPUT_PATH);
		}
	}

	public static JdbcTemplate createJdbcTemplateFromDataSource(DataSource dataSource) {
		JdbcTemplate ret = new JdbcTemplate(dataSource);
		ret.afterPropertiesSet();
		return ret;
	}

}
