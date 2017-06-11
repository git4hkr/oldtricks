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
package oldtricks.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 周期タスクExecutorです。
 * 
 * @author kubota
 * 
 */
public class PeriodecExecutor extends AbstractCyclicExecutor {
	private static Logger LOG = LoggerFactory.getLogger(PeriodecExecutor.class);
	private ScheduledExecutorService scheduledExecutor;
	/** 周期 */
	private long periodMsec;

	/**
	 * @throws Exception
	 *             例外発生時
	 * @see jp.lencois.executor.PeriodecExecutor#start()
	 */
	public void start() throws Exception {
		LOG.info("start. " + toString());
		super.start();
		scheduledExecutor = Executors.newScheduledThreadPool(1);
		scheduledExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				fireTask();
			}
		}, 0L, periodMsec, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown(long shutdownTimeout) {
		scheduledExecutor.shutdownNow();
		super.shutdown(shutdownTimeout);
	}

	/**
	 * periodMsecを取得します。
	 * 
	 * @return periodMsec
	 */
	public long getPeriodMsec() {
		return periodMsec;
	}

	/**
	 * periodMsecを設定します。
	 * 
	 * @param periodMsec
	 *            periodMsec
	 */
	public void setPeriodMsec(long periodMsec) {
		this.periodMsec = periodMsec;
	}

	@Override
	public String toString() {
		return "PeriodecExecutor [scheduledExecutor=" + scheduledExecutor + ", periodMsec=" + periodMsec
				+ ", ThreadsCore=" + getThreadsSize() + ", Name=" + getName() + ", Limit=" + getLimit() + "]";
	}

}
