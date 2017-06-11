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



import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ClassUtils;

public class CsvHeaderStringParser {

	public static class ParseErrorException extends Exception {

		public ParseErrorException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public static class OrderBy {

		private int sortOrder;
		private String column;

		public OrderBy(int sortOrder, String column) {
			super();
			this.sortOrder = sortOrder;
			this.column = column;
		}

		public int getSortOrder() {
			return sortOrder;
		}

		public String getColumn() {
			return column;
		}
	}

	public static class CsvHeader {
		private String columnName;
		private String type;
		private OrderBy orderby;
		private AssertionHandler handler = new DefaultAssertionHandler();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public OrderBy getOrderby() {
			return orderby;
		}

		public void setOrderby(OrderBy orderby) {
			this.orderby = orderby;
		}

		public AssertionHandler getHandler() {
			return handler;
		}

		public void setHandler(AssertionHandler handler) {
			this.handler = handler;
		}
	}

	public static CsvHeader parse(final String headerString) throws ParseErrorException {
		try {
			String[] entities = headerString.split("@");
			CsvHeader ret = new CsvHeader();
			if (entities.length > 0)
				ret.setColumnName(entities[0]);
			for (String entity : entities) {
				if (entity.startsWith("orderbydesc:")) {
					String[] orderByEntity = entity.split(":");
					if (orderByEntity.length > 1) {
						int order = Integer.parseInt(orderByEntity[1]);
						OrderBy orderby = new OrderBy(order, ret.getColumnName() + " DESC");
						ret.setOrderby(orderby);
					}
				}
				if (entity.startsWith("orderby:")) {
					String[] orderByEntity = entity.split(":");
					if (orderByEntity.length > 1) {
						int order = Integer.parseInt(orderByEntity[1]);
						OrderBy orderby = new OrderBy(order, ret.getColumnName());
						ret.setOrderby(orderby);
					}
				}
				if (entity.startsWith("handler:")) {
					String[] handerEntity = entity.split(":");
					if (handerEntity.length > 1) {
						String className = StringUtils.strip(handerEntity[1]);
						String[] args = null;
						if (handerEntity.length > 2) {
							args = (String[]) ArrayUtils.subarray(handerEntity, 2, handerEntity.length);
						}
						try {
							@SuppressWarnings("unchecked")
							Class<AssertionHandler> clazz = (Class<AssertionHandler>) ClassUtils.forName(className,
									null);
							AssertionHandler handler = clazz.newInstance();
							handler.setArgs(args);
							ret.setHandler(handler);
						} catch (ClassNotFoundException e) {
							throw new ClassNotFoundException("handler class not found. handler=[" + className + "], ");
						}
					}
				}
				if (entity.startsWith("type:")) {
					String[] typeEntity = entity.split(":");
					if (typeEntity.length > 1) {
						ret.setType(StringUtils.strip(typeEntity[1]));
					}
				}
			}
			return ret;
		} catch (Exception e) {
			throw new ParseErrorException("csv header parse error.", e);
		}
	}
}
