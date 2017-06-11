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
package oldtricks.blogic.interceptor;

import oldtricks.util.StringUtil;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class InterceptorSupport implements MethodInterceptor {

	public static String getTargetMethodName(MethodInvocation invocation) {
		String ret = getClassNameExcludeCGLIB(invocation.getThis().getClass().getCanonicalName()) + "."
				+ invocation.getMethod().getName() + "()";
		return ret;
	}

	/**
	 * CGLIBが作ったクラスの場合はクラス名が変わるので、もとのクラス名を切り出す。$$EnhancerByCGLIB
	 * という文字列が含まれた場合その前までを返却する。
	 *
	 * @param className
	 * @return CGLIBが加工する前のクラス名
	 */
	public static String getClassNameExcludeCGLIB(String className) {
		return StringUtil.substringBeforeLast(className, "$$EnhancerByCGLIB");
	}

	/**
	 * 入力されたオブジェクトをリフレクションしてプロパティ値を文字列で返却します。
	 * 
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		return ToStringBuilder.reflectionToString(obj, new CustomToStringStyle());
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
			this.setUseIdentityHashCode(false);
		}

	}

}
