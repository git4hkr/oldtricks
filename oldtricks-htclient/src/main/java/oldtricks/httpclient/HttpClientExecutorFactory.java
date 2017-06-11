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
package oldtricks.httpclient;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientExecutorFactory {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private HttpParams params = new BasicHttpParams();
		private HttpVersion version = HttpVersion.HTTP_1_1;
		private int connectTimeout = 3000;
		private int soTimeout = 10000;
		private int socketBufferSize = -1;
		private boolean tcpNoDelay = false;
		private boolean soReuseaddr = true;
		private boolean staleCheckingEnabled = true;

		private boolean pooling = true;
		private int poolConnMaxTotal = 100;
		private int poolConnMaxPerRoute = 1;

		public Executor build() {

			ClientConnectionManager cm = null;
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
					.getSocketFactory()));
			if (pooling) {
				PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager(
						schemeRegistry);
				poolingClientConnectionManager.setMaxTotal(poolConnMaxTotal);
				poolingClientConnectionManager
						.setDefaultMaxPerRoute(poolConnMaxPerRoute);
				cm = poolingClientConnectionManager;
			} else {
				cm = new BasicClientConnectionManager(schemeRegistry);
			}
			DefaultHttpClient httpClient = new DefaultHttpClient(cm);
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, version);
			HttpConnectionParams.setConnectionTimeout(params, connectTimeout);
			HttpConnectionParams.setSocketBufferSize(params, socketBufferSize);
			HttpConnectionParams.setSoTimeout(params, soTimeout);
			HttpConnectionParams.setTcpNoDelay(params, tcpNoDelay);
			HttpConnectionParams.setSoReuseaddr(params, soReuseaddr);
			HttpConnectionParams.setStaleCheckingEnabled(params,
					staleCheckingEnabled);
			httpClient.setParams(params);
			return new Executor(httpClient);
		}

		/**
		 * コネクションプールを行う。デフォルトはTrue。コネクションプールを行わなかった場合、{@link HttpClient}
		 * はスレッドセーフでなくなる。
		 *
		 * @param poolingEnabled
		 * @return ビルダー
		 */
		public Builder pooling(boolean poolingEnabled) {
			this.pooling = poolingEnabled;
			return this;
		}

		/**
		 * コネクションプール有効時、全サーバトータルの最大コネクション数。デフォルト100。
		 *
		 * @param poolConnMaxTotal
		 * @return
		 */
		public Builder poolConnMaxTotal(int poolConnMaxTotal) {
			this.poolConnMaxTotal = poolConnMaxTotal;
			return this;
		}

		/**
		 * コネクションプール有効時、サーバあたりの最大コネクション数。デフォルト1。
		 *
		 * @param poolConnMaxPerRoute
		 * @return
		 */
		public Builder poolConnMaxPerRoute(int poolConnMaxPerRoute) {
			this.poolConnMaxPerRoute = poolConnMaxPerRoute;
			return this;
		}

		public Builder httpVersion(HttpVersion version) {
			this.version = version;
			return this;
		}

		public Builder userAgent(String userAgent) {
			params.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
			return this;
		}

		public Builder addParam(String name, Object value) {
			params.setParameter(name, value);
			return this;
		}

		public Builder connectTO(int connectTimeoutMs) {
			this.connectTimeout = connectTimeoutMs;
			return this;
		}

		public Builder socketTO(int soTimeoutMs) {
			this.soTimeout = soTimeoutMs;
			return this;
		}

		public Builder soBufferSize(int soBufferSize) {
			this.socketBufferSize = soBufferSize;
			return this;
		}

		public Builder tcpNoDelay(boolean tcpNoDelay) {
			this.tcpNoDelay = tcpNoDelay;
			return this;
		}

		public Builder soReuseAddr(boolean soReuseAddr) {
			this.soReuseaddr = soReuseAddr;
			return this;
		}

		public Builder staleCheckingEnabled(boolean staleCheckingEnabled) {
			this.staleCheckingEnabled = staleCheckingEnabled;
			return this;
		}
	}
}
