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
package oldtricks.test.db;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import oldtricks.test.csv.CSVReader;
import oldtricks.test.db.CsvHeaderStringParser.CsvHeader;
import oldtricks.test.db.CsvHeaderStringParser.ParseErrorException;
import oldtricks.test.jdbc.JdbcTestUtil;
import oldtricks.test.jdbc.SQL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

public class DatabaseTestSupport {
	private static final String POST_DIR_NAME = "post";
	private static final String PRE_DIR_NAME = "pre";
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseTestSupport.class);
	private static String CSV_ENCODING = "MS932";
	private String dbName;
	private JdbcTemplate jdbcTemplate;

	public DatabaseTestSupport(String dbName, JdbcTemplate jdbcTemplate) {
		super();
		Assert.notNull(dbName, "dbName must not be null.");
		Assert.notNull(jdbcTemplate, "jdbcTemplate must not be null.");
		this.dbName = dbName;
		this.jdbcTemplate = jdbcTemplate;
	}

	public void preProcess(File _inputDir, File _outputDir) throws Exception {
		final File inputDir = getInputPreDir(_inputDir);
		final File outputDir = getOutputPreDir(_outputDir);
		if (inputDir.exists() && inputDir.isDirectory()) {
			File[] files = inputDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".csv"));
			for (File file : files) {
				// 出力系ディレクトリに入力系ファイルをコピー
				FileUtils.deleteDirectory(outputDir);
				FileUtils.forceMkdir(outputDir);
				FileUtils.copyFileToDirectory(file, outputDir);
				// テーブルのインポート
				String tableName = StringUtils.substringBeforeLast(file.getName(), ".");
				String csvFileName = file.getAbsolutePath();
				// テーブルのクリーニング
				deleteFromTables(tableName);
				// テーブルにCSVからインポート
				importTableFromCsv(tableName, csvFileName);
			}
		}
	}

	public void postProcess(File _inputDir, File _outputDir) throws Exception {
		final File inputDir = getInputPostDir(_inputDir);
		final File outputDir = getOutputPostDir(_outputDir);
		if (inputDir.exists() && inputDir.isDirectory()) {
			File[] files = inputDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".csv"));
			for (File file : files) {
				FileUtils.deleteDirectory(outputDir);
				FileUtils.forceMkdir(outputDir);
				final String tableName = StringUtils.substringBeforeLast(file.getName(), ".");
				File expect = new File(outputDir, tableName + "-expect.csv");
				File actual = new File(outputDir, tableName + "-actual.csv");
				FileUtils.copyFile(file, expect);
				// テーブルをCSVに出力
				exportTableToCsv(tableName, expect.getAbsolutePath(), actual.getAbsolutePath());
				// テスト結果（CSV）の検査
				assertEqualsCsvFile(expect.getAbsolutePath(), actual.getAbsolutePath());
			}
		}
	}

	public File getOutputPreDir(File outputDir) {
		StringBuilder builder = new StringBuilder();
		builder.append(PRE_DIR_NAME);
		builder.append(File.separator);
		builder.append(dbName);
		return new File(outputDir, builder.toString());
	}

	public File getInputPreDir(File inputDir) {
		StringBuilder builder = new StringBuilder();
		builder.append(PRE_DIR_NAME);
		builder.append(File.separator);
		builder.append(dbName);
		return new File(inputDir, builder.toString());
	}

	public File getOutputPostDir(File outputDir) {
		StringBuilder builder = new StringBuilder();
		builder.append(POST_DIR_NAME);
		builder.append(File.separator);
		builder.append(dbName);
		return new File(outputDir, builder.toString());
	}

	public File getInputPostDir(File inputDir) {
		StringBuilder builder = new StringBuilder();
		builder.append(POST_DIR_NAME);
		builder.append(File.separator);
		builder.append(dbName);
		return new File(inputDir, builder.toString());
	}

	protected int deleteFromTables(String tableName) {
		return JdbcTestUtil.deleteFromTables(jdbcTemplate, tableName);
	}

	public void importTableFromCsv(final String tableName, final String csvFileName) throws IOException,
			ParseErrorException {
		CSVReader csvReader = null;
		try {
			csvReader = CSVReader.newCsvReader(csvFileName, CSV_ENCODING);
			final String[] header = csvReader.readNext();

			String query = new SQL() {
				{
					INSERT_INTO(tableName);
					for (String column : header) {
						CsvHeader csvHead = CsvHeaderStringParser.parse(column);
						VALUES(csvHead.getColumnName(), "?");
					}
				}
			}.toString();
			final List<Object[]> list = new ArrayList<Object[]>();
			String[] line = null;
			while ((line = csvReader.readNext()) != null) {
				list.add(line);
			}
			jdbcTemplate.batchUpdate(query, list);
		} finally {
			IOUtils.closeQuietly(csvReader);
		}
	}

	protected void exportTableToCsv(String tableName, String expect, String actual) throws IOException,
			ParseErrorException, SQLException {
		LOG.info("export table from [" + tableName + "] to table[" + actual + "]");
		CSVReader expectCsv = CSVReader.newCsvReader(expect, CSV_ENCODING);
		String[] headers = expectCsv.readNext();
		List<String> columnList = new ArrayList<String>();
		SortedMap<Integer, String> orderByList = new TreeMap<Integer, String>();
		for (int i = 0; i < headers.length; i++) {
			CsvHeader header = CsvHeaderStringParser.parse(headers[i]);
			columnList.add(header.getColumnName());
			if (header.getOrderby() != null) {
				orderByList.put(header.getOrderby().getSortOrder(), header.getOrderby().getColumn());
			}
		}
		String[] columns = columnList.toArray(new String[0]);
		String[] orderByColumn = orderByList.values().toArray(new String[0]);
		JdbcTestUtil.exportTableToCsv(jdbcTemplate, tableName, columns, orderByColumn, actual);
	}

	protected void assertEqualsCsvFile(String expectCsvFile, String actualCsvFile) {
		try {
			List<String[]> expect = CSVReader.newCsvReader(expectCsvFile).readAll();
			List<String[]> actual = CSVReader.newCsvReader(actualCsvFile).readAll();
			if (expect.size() != actual.size()) {
				fail("unmatch row count. expect=" + expect.size() + ", actual=" + actual.size() + ", expect file=["
						+ expectCsvFile + "], actual file=[" + actualCsvFile + "]");
			}
			List<CsvHeader> expectHeaders = new ArrayList<CsvHeaderStringParser.CsvHeader>();
			List<CsvHeader> actualHeaders = new ArrayList<CsvHeaderStringParser.CsvHeader>();
			int i = 0;
			int j = 0;
			for (i = 0; i < expect.size(); i++) {
				try {
					String[] expectRow = expect.get(i);
					String[] actualRow = actual.get(i);
					// カラム数のチェック
					if (expectRow.length != actualRow.length) {
						fail("unmatch column length in row[" + (i + 1) + "]");
					}
					for (j = 0; j < expectRow.length; j++) {
						String expectValue = null;
						String actualValue = null;
						if (i == 0) {
							// ヘッダー値の取得
							CsvHeader expectheader = CsvHeaderStringParser.parse(expectRow[j]);
							expectValue = expectheader.getColumnName();
							expectHeaders.add(expectheader);
							CsvHeader actualheader = CsvHeaderStringParser.parse(actualRow[j]);
							actualValue = actualheader.getColumnName();
							actualHeaders.add(actualheader);
							new DefaultAssertionHandler().assertEquals(expectValue, actualValue, i + 1, j + 1);
						} else {
							// テーブル値の取得
							expectValue = expectRow[j];
							actualValue = actualRow[j];
							expectHeaders.get(j).getHandler().assertEquals(expectValue, actualValue, i + 1, j + 1);
						}
					}
				} catch (Exception e) {
					fail(e.getMessage() + " in row[" + (i + 1) + "][" + (j + 1) + "]");
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * dbNameを取得します。
	 * 
	 * @return dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * dbNameを設定します。
	 * 
	 * @param dbName
	 *            dbName
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * jdbcTemplateを取得します。
	 * 
	 * @return jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * jdbcTemplateを設定します。
	 * 
	 * @param jdbcTemplate
	 *            jdbcTemplate
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
