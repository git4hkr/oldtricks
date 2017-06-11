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
package oldtricks.test.jdbc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oldtricks.test.csv.CSVReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.jdbc.JdbcTestUtils;

public abstract class JdbcTestUtil extends JdbcTestUtils {

	public static int exportTableToCsv(JdbcTemplate jdbcTemplate,
			final String tableName, final String outputFileName)
			throws IOException, SQLException {
		return exportTableToCsv(jdbcTemplate, tableName, null, null,
				outputFileName, "MS932");
	}

	public static int exportTableToCsv(JdbcTemplate jdbcTemplate,
			final String tableName, final String[] orderByColumns,
			final String outputFileName) throws IOException, SQLException {
		return exportTableToCsv(jdbcTemplate, tableName, null, orderByColumns,
				outputFileName, "MS932");
	}

	public static int exportTableToCsv(JdbcTemplate jdbcTemplate,
			final String tableName, final String[] columns,
			final String[] orderByColumns, final String outputFileName)
			throws IOException, SQLException {
		return exportTableToCsv(jdbcTemplate, tableName, columns,
				orderByColumns, outputFileName, "MS932");
	}

	public static int exportTableToCsv(JdbcTemplate jdbcTemplate,
			final String tableName, final String[] columns,
			final String[] orderByColumns, final String outputFileName,
			final String encoding) throws IOException, SQLException {
		final String query = new SQL() {
			{
				if (columns == null)
					SELECT("*");
				else
					for (String column : columns) {
						SELECT(column);
					}
				FROM(tableName);
				if (orderByColumns != null)
					for (String column : orderByColumns) {
						ORDER_BY(column);
					}
			}
		}.toString();

		final SqlRowSet rowset = jdbcTemplate.queryForRowSet(query);
		SqlRowSetCsvWriter writer = null;
		try {
			writer = new SqlRowSetCsvWriter(new FileWriterWithEncoding(
					outputFileName, encoding));
			return writer.writeAll(rowset, true);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static void importTableFromCsv(JdbcTemplate jdbcTemplate,
			final String tableName, final String csvFileName)
			throws IOException {
		importTableFromCsv(jdbcTemplate, tableName, csvFileName, "MS932");
	}

	public static void importTableFromCsv(JdbcTemplate jdbcTemplate,
			final String tableName, final String csvFileName, String encoding)
			throws IOException {
		CSVReader csvReader = null;
		try {
			csvReader = CSVReader.newCsvReader(csvFileName, encoding);
			final String[] header = csvReader.readNext();
			String query = new SQL() {
				{
					INSERT_INTO(tableName);
					for (String column : header) {
						VALUES(column, "?");
					}
				}
			}.toString();
			final List<Object[]> list = new ArrayList<Object[]>();
			String[] line = null;
			while ((line = csvReader.readNext()) != null) {
				for (int i = 0; i < line.length; i++) {
					String string = line[i];
					if (StringUtils.isEmpty(string))
						line[i] = null;
				}
				list.add(line);
			}
			jdbcTemplate.batchUpdate(query, list);
		} finally {
			IOUtils.closeQuietly(csvReader);
		}
	}
}
