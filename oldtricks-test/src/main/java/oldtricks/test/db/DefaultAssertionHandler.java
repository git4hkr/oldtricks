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

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

public class DefaultAssertionHandler implements AssertionHandler {

	public void assertEquals(String expect, String actual, int row, int column) {
		if (!StringUtils.equals(expect, actual)) {
			fail(expect, actual, row, column);
		}
	}

	protected void fail(String expect, String actual, int row, int column) {
		Assert.fail("unmatch row-column[" + row + "][" + column + "] value. expect=[" + expect + "], actual=[" + actual + "]");
	}

	public void setArgs(String[] args) {

	}

}
