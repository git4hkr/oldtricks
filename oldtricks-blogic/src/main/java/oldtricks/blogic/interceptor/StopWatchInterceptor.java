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

import oldtricks.tool.Stopwatch;
import oldtricks.util.StringUtil;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWatchInterceptor implements MethodInterceptor {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(StopWatchInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Stopwatch stopwatch = new Stopwatch().start();
		try {
			return invocation.proceed();
		} finally {
			LOG.info("{}.{}() finished. elapsed[{}]", new Object[] {
					getClassNameExcludeCGLIB(invocation.getThis().getClass().getCanonicalName()),
					invocation.getMethod().getName(), stopwatch.stop().toString() });
		}
	}

	/**
	 * CGLIBが作ったクラスの場合はクラス名が変わるので、もとのクラス名を切り出す。$$EnhancerByCGLIB
	 * という文字列が含まれた場合その前までを返却する。
	 * 
	 * @param className
	 * @return CGLIBが加工する前のクラス名
	 */
	static String getClassNameExcludeCGLIB(String className) {
		return StringUtil.substringBeforeLast(className, "$$EnhancerByCGLIB");
	}
}
