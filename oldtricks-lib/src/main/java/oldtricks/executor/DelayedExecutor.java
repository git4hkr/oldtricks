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
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 遅延タスクExecutorです。
 *
 * @author $Author: kubota $
 *
 */
public class DelayedExecutor extends ExecutorSupport {

	private static final long serialVersionUID = -7987907230834052192L;
	private ScheduledExecutorService scheduledExecutorService;

	public DelayedExecutor() {
		super();
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(createThreadFactory());
		setExecutor(scheduledExecutorService);
	}

	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
		return scheduledExecutorService.scheduleWithFixedDelay(task, initialDelay, delay, unit);
	}

	@Deprecated
	@Override
	public Future<?> submit(Runnable task) throws TooManyTaskException, RejectedExecutionException {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void execute(Runnable command) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "DelayedExecutor [scheduledExecutorService=" + scheduledExecutorService + ", Name=" + getName()
				+ ", ThreadsSize=" + getThreadsSize() + ", MaxThreadsSize=" + getMaxThreadsSize() + ", QueueCapacity="
				+ getQueueCapacity() + "]";
	}

}
