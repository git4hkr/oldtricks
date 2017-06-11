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
package oldtricks.tool;

import org.junit.Test;

public class ToggleTest {

	@Test
	public void 正常系() {
		// A,B.Cを3階ずつ出す
		Toggle<String> toggle = new Toggle<String>(3, "A", "B", "C");
		for (int i = 0; i < 10; i++, toggle.toggled()) {
			System.out.println(toggle);
		}
	}

	@Test
	public void 準正常系() {
		// しきい値だけ指定して、値を指定しない
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Toggle toggle = new Toggle(1);
		System.out.println(toggle);
		System.out.println(toggle.toggled());
	}
}
