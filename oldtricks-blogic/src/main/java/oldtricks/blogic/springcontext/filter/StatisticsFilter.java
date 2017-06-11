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
package oldtricks.blogic.springcontext.filter;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import oldtricks.blogic.BLogicStatistic;
import oldtricks.blogic.BLogicStatisticConfig;
import oldtricks.blogic.springcontext.BLogicFilterWithLifeCycle;
import oldtricks.blogic.statistics.StatisticsSupport;
import oldtricks.tool.Stopwatch;

public class StatisticsFilter extends StatisticsSupport implements BLogicFilterWithLifeCycle {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsFilter.class);
	protected boolean dumpOnExit = true;

	protected String getKey(Method method) {
		BLogicStatistic anno = method.getAnnotation(BLogicStatistic.class);
		String key = null;
		if (anno != null)
			key = anno.value();
		return key;
	}

	@Override
	public boolean accept(Method method) {
		try {
			return method.getAnnotation(BLogicStatistic.class) != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Object intercept(Object target, Method method, Object[] args, Next next) throws Throwable {
		BLogicStatistic anno = method.getAnnotation(BLogicStatistic.class);
		if (anno == null) {
			return next.invoke(target, method, args);
		}
		Stopwatch stopwatch = new Stopwatch().start();
		try {
			return next.invoke(target, method, args);
		} finally {
			String key = getKey(method);
			addValue(key, stopwatch.elapsedTime(TimeUnit.MILLISECONDS), anno.windowSize());
		}
	}

	@Override
	public void init(Object target, ApplicationContext applicationContext) throws Throwable {
		BLogicStatisticConfig config = target.getClass().getAnnotation(BLogicStatisticConfig.class);
		if (config != null)
			dumpOnExit = config.dumpOnExit();
	}

	@Override
	public void destory() throws Throwable {
		if (dumpOnExit) {
			LOG.info(System.lineSeparator() + "--------- function call elapsed time(ms) -------------------------"
					+ System.lineSeparator() + dumpAllStat());
		}
		clear();
	}

}
