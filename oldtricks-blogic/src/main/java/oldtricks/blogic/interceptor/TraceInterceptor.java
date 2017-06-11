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

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceInterceptor extends InterceptorSupport {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(TraceInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Stopwatch stopwatch = new Stopwatch().start();
		try {
			System.out.println(invocation.getThis());
			LOG.info("{} input[{}]({})",
					new Object[] { getTargetMethodName(invocation), toString(invocation.getArguments()) });
			Object ret = invocation.proceed();
			LOG.info("{} output[{}]", getTargetMethodName(invocation), toString(ret));
			return ret;
		} finally {
			LOG.info("{}.{}() finished. elapsed[{}]", new Object[] {
					getClassNameExcludeCGLIB(invocation.getThis().getClass().getCanonicalName()),
					invocation.getMethod().getName(), stopwatch.stop().toString() });
		}
	}
}
