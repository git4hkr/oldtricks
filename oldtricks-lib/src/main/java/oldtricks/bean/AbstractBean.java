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
package oldtricks.bean;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AbstractBean implements Serializable {

	private static final long serialVersionUID = 2739488863430163974L;

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, new CustomToStringStyle());
	}

	private static final class CustomToStringStyle extends ToStringStyle {

		private static final long serialVersionUID = 1L;

		CustomToStringStyle() {
			super();
			this.setContentStart("[");
			this.setFieldSeparator(" ,");
			this.setFieldSeparatorAtStart(false);
			this.setFieldSeparatorAtEnd(false);
			this.setUseShortClassName(true);
		}

	}
}
