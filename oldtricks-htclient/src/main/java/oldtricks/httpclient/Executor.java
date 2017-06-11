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

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;

public class Executor {

	private final HttpClient httpclient;
	private final BasicHttpContext localContext;
	private final AuthCache authCache;

	private CredentialsProvider credentialsProvider;
	private CookieStore cookieStore;

	public Executor(final HttpClient httpclient) {
		super();
		this.httpclient = httpclient;
		this.localContext = new BasicHttpContext();
		this.authCache = new BasicAuthCache();
	}

	public void shutdown() {
		httpclient.getConnectionManager().shutdown();
	}

	public Executor auth(final AuthScope authScope, final Credentials creds) {
		if (this.credentialsProvider == null) {
			this.credentialsProvider = new BasicCredentialsProvider();
		}
		this.credentialsProvider.setCredentials(authScope, creds);
		return this;
	}

	public Executor auth(final HttpHost host, final Credentials creds) {
		AuthScope authScope = host != null ? new AuthScope(host)
				: AuthScope.ANY;
		return auth(authScope, creds);
	}

	public Executor authPreemptive(final HttpHost host) {
		this.authCache.put(host, new BasicScheme());
		return this;
	}

	public Executor auth(final Credentials cred) {
		return auth(AuthScope.ANY, cred);
	}

	public Executor auth(final String username, final String password) {
		return auth(new UsernamePasswordCredentials(username, password));
	}

	public Executor auth(final String username, final String password,
			final String workstation, final String domain) {
		return auth(new NTCredentials(username, password, workstation, domain));
	}

	public Executor auth(final HttpHost host, final String username,
			final String password) {
		return auth(host, new UsernamePasswordCredentials(username, password));
	}

	public Executor auth(final HttpHost host, final String username,
			final String password, final String workstation, final String domain) {
		return auth(host, new NTCredentials(username, password, workstation,
				domain));
	}

	public Executor clearAuth() {
		if (this.credentialsProvider != null) {
			this.credentialsProvider.clear();
		}
		return this;
	}

	public Executor cookieStore(final CookieStore cookieStore) {
		this.cookieStore = cookieStore;
		return this;
	}

	public Executor clearCookies() {
		if (this.cookieStore != null) {
			this.cookieStore.clear();
		}
		return this;
	}

	public Response execute(final Request req) throws ClientProtocolException,
			IOException {
		this.localContext.setAttribute(ClientContext.CREDS_PROVIDER,
				this.credentialsProvider);
		this.localContext
				.setAttribute(ClientContext.AUTH_CACHE, this.authCache);
		this.localContext.setAttribute(ClientContext.COOKIE_STORE,
				this.cookieStore);
		HttpRequestBase httprequest = req.getHttpRequest();
		httprequest.reset();
		return new Response(this.httpclient.execute(httprequest,
				this.localContext));
	}

}