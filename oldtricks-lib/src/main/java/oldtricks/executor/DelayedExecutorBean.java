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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * springframeworkのbeanとして{@link DelayedExecutor}を利用するための拡張です。
 *
 * @author $Author: kubota $
 *
 */
public class DelayedExecutorBean extends DelayedExecutor {
	private static final long serialVersionUID = -6021894701126849554L;
	private long shutdownTimeout = 0;

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		start();
	}

	@PreDestroy
	public void destroy() throws Exception {
		shutdown(shutdownTimeout);
	}

	/**
	 * shutdownTimeout（秒）を取得します。
	 *
	 * @return shutdownTimeout
	 */
	public long getShutdownTimeout() {
		return shutdownTimeout;
	}

	/**
	 * shutdownTimeout(秒)を設定します。
	 *
	 * @param shutdownTimeout
	 *            shutdownTimeout
	 */
	public void setShutdownTimeout(long shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

}
