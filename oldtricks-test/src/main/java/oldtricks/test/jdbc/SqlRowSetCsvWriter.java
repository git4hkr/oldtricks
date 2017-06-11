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
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import oldtricks.test.csv.CSVWriter;

import org.joda.time.DateTime;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

public class SqlRowSetCsvWriter extends CSVWriter {

	public SqlRowSetCsvWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
		super(writer, separator, quotechar, escapechar, lineEnd);
	}

	public SqlRowSetCsvWriter(Writer writer, char separator, char quotechar, char escapechar) {
		super(writer, separator, quotechar, escapechar);
	}

	public SqlRowSetCsvWriter(Writer writer, char separator, char quotechar, String lineEnd) {
		super(writer, separator, quotechar, lineEnd);
	}

	public SqlRowSetCsvWriter(Writer writer, char separator, char quotechar) {
		super(writer, separator, quotechar);
	}

	public SqlRowSetCsvWriter(Writer writer, char separator) {
		super(writer, separator);
	}

	public SqlRowSetCsvWriter(Writer writer) {
		super(writer);
	}

	public int writeAll(SqlRowSet rs, boolean includeColumnNames) throws SQLException, IOException {
		if (includeColumnNames) {
			writeNext(getColumnNames(rs));
		}
		int lines = 0;
		while (rs.next()) {
			lines++;
			writeNext(getColumnValues(rs));
		}
		return lines;
	}

	static String[] getColumnNames(SqlRowSet rs) {
		List<String> names = new ArrayList<String>();
		SqlRowSetMetaData metadata = rs.getMetaData();

		for (int i = 0; i < metadata.getColumnCount(); i++) {
			names.add(metadata.getColumnName(i + 1) + "@type:" + metadata.getColumnTypeName(i + 1));
		}

		String[] nameArray = new String[names.size()];
		return names.toArray(nameArray);
	}

	static String[] getColumnValues(SqlRowSet rs) throws InvalidResultSetAccessException, SQLException, IOException {
		List<String> values = new ArrayList<String>();
		SqlRowSetMetaData metadata = rs.getMetaData();

		for (int i = 0; i < metadata.getColumnCount(); i++) {
			values.add(getColumnValue(rs, metadata.getColumnType(i + 1), i + 1));
		}

		String[] valueArray = new String[values.size()];
		return values.toArray(valueArray);
	}

	static String getColumnValue(SqlRowSet rs, int colType, int colIndex) throws SQLException, IOException {

		String value = "";

		switch (colType) {
		case Types.BIT:
		case Types.JAVA_OBJECT:
			value = handleObject(rs.getObject(colIndex));
			break;
		case Types.BOOLEAN:
			boolean b = rs.getBoolean(colIndex);
			value = Boolean.valueOf(b).toString();
			break;
		case Types.BIGINT:
			value = handleLong(rs, colIndex);
			break;
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
		case Types.NUMERIC:
			value = handleBigDecimal(rs.getBigDecimal(colIndex));
			break;
		case Types.INTEGER:
		case Types.TINYINT:
		case Types.SMALLINT:
			value = handleInteger(rs, colIndex);
			break;
		case Types.DATE:
			value = handleDate(rs, colIndex);
			break;
		case Types.TIME:
			value = handleTime(rs.getTime(colIndex));
			break;
		case Types.TIMESTAMP:
			value = handleTimestamp(rs.getTimestamp(colIndex));
			break;
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.CHAR:
			value = rs.getString(colIndex);
			break;
		default:
			value = "";
		}

		if (value == null) {
			value = "";
		}

		return value;
	}

	static String handleObject(Object obj) {
		return obj == null ? "" : String.valueOf(obj);
	}

	static String handleBigDecimal(BigDecimal decimal) {
		return decimal == null ? "" : decimal.toString();
	}

	static String handleLong(SqlRowSet rs, int columnIndex) throws SQLException {
		long lv = rs.getLong(columnIndex);
		return rs.wasNull() ? "" : Long.toString(lv);
	}

	static String handleInteger(SqlRowSet rs, int columnIndex) throws SQLException {
		int i = rs.getInt(columnIndex);
		return rs.wasNull() ? "" : Integer.toString(i);
	}

	static String handleDate(SqlRowSet rs, int columnIndex) throws SQLException {
		java.sql.Date date = rs.getDate(columnIndex);
		String value = null;
		if (date != null) {
			value = new DateTime(date.getTime()).toString("yyyy/M/d");
		}
		return value;
	}

	static String handleTime(Time time) {
		return time == null ? null : time.toString();
	}

	static String handleTimestamp(Timestamp timestamp) {
		String value = null;
		if (timestamp != null)
			value = new DateTime(timestamp.getTime()).toString("yyyy/M/d HH:mm:ss");
		return value;
	}
}
