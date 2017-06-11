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

import java.util.concurrent.TimeUnit;

import oldtricks.blogic.statistics.StatisticsSupport;
import oldtricks.tool.Stopwatch;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.DisposableBean;

public class StatisticsInterceptor extends StatisticsSupport implements DisposableBean, MethodInterceptor {
	protected boolean dumpOnExit = true;
	private int windowSize = DEFAULT_WINDOW_SIZE;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Stopwatch stopwatch = new Stopwatch().start();
		try {
			return invocation.proceed();
		} finally {
			String key = getKey(invocation);
			addValue(key, stopwatch.elapsedTime(TimeUnit.MILLISECONDS), windowSize);
		}
	}

	protected String getKey(MethodInvocation invocation) {
		String key = invocation.getMethod().getName();
		return key;
	}

	@Override
	public void destroy() throws Exception {
		if (dumpOnExit) {
			System.out.println("--------- function call elapsed time(ms) -------------------------");
			System.out.println(dumpAllStat());
		}
		clear();
	}

	/**
	 * windowSizeを取得します。
	 *
	 * @return windowSize
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * windowSizeを設定します。
	 *
	 * @param windowSize
	 *            windowSize
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

}
