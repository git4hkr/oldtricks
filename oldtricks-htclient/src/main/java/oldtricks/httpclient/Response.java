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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class Response {

	private final HttpResponse response;
	private boolean consumed;

	Response(final HttpResponse response) {
		super();
		this.response = response;
	}

	private void assertNotConsumed() {
		if (this.consumed) {
			throw new IllegalStateException("Response content has been already consumed");
		}
	}

	public int status() throws IOException {
		return returnResponse().getStatusLine().getStatusCode();
	}

	/**
	 * 生のStatusコードを取得します。レスポンス処理の挙動を理解している場合のみ利用し、通常は{@link #status()}
	 * を利用してください。
	 * 
	 * @return ステータスコード
	 * @throws IOException
	 */
	@Deprecated
	public int rowStatus() throws IOException {
		return response.getStatusLine().getStatusCode();
	}

	/**
	 * 生の{@link HttpEntity} を取得します。レスポンス処理の挙動を理解している場合のみ利用し、通常は
	 * {@link #returnResponse()}経由で {@link HttpEntity}を取得してください。
	 * 
	 * @return 生の{@link HttpEntity}です。ただし、{@link #returnResponse()}や
	 *         {@link #status()}を呼び出した後の場合はメモリ上にロードした{@link HttpEntity}を返却します。
	 * @throws IOException
	 */
	@Deprecated
	public HttpEntity getRowEntity() throws IOException {
		return response.getEntity();
	}

	/**
	 * 未消費のストリームがあれば消費します。
	 */
	private void dispose() {
		if (this.consumed) {
			return;
		}
		try {
			EntityUtils.consume(this.response.getEntity());
		} catch (Exception ignore) {
		} finally {
			this.consumed = true;
		}
	}

	/**
	 * {@link ResponseHandler}で定義したカスタムレスポンス処理を行います。
	 * 
	 * @param handler
	 *            カスタムレスポンス処理
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public <T> T handleResponse(final ResponseHandler<T> handler) throws ClientProtocolException, IOException {
		assertNotConsumed();
		try {
			return handler.handleResponse(this.response);
		} finally {
			dispose();
		}
	}

	/**
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Content returnContent() throws ClientProtocolException, IOException {
		return handleResponse(new ResponseHandler<Content>() {

			public Content handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
				}
				if (entity != null) {
					return new Content(EntityUtils.toByteArray(entity), ContentType.getOrDefault(entity));
				} else {
					return Content.NO_CONTENT;
				}
			}
		});
	}

	/**
	 * レスポンス処理を行います。<BR>
	 * ストリーム処理をプログラマから隠ぺいするため、ストリームから受信したデータを強制的にメモリ上にロードします。そのためメモリを多く使用しますが、
	 * リソースの開放漏れを防げます。 通常はこちらを利用してください。より効率的にメモリを使用して処理したい場合は@li
	 * {@link #handleResponse(ResponseHandler)}を利用してカスタムレスポンス処理を実装するか、
	 * {@link #getRowEntity()} を利用して、メモリ展開前のEntityを取得してください。
	 * 
	 * @return
	 * @throws IOException
	 */
	public HttpResponse returnResponse() throws IOException {
		if (this.consumed) {
			return this.response;
		}
		try {
			HttpEntity entity = this.response.getEntity();
			if (entity != null) {
				this.response.setEntity(new ByteArrayEntity(EntityUtils.toByteArray(entity), ContentType
						.getOrDefault(entity)));
			}
			return this.response;
		} finally {
			this.consumed = true;
		}
	}

	/**
	 * レスポンスの内容をファイルに保存します。
	 * 
	 * @param file
	 *            出力先ファイル
	 * @throws IOException
	 */
	public void saveContent(final File file) throws IOException {
		assertNotConsumed();
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
		FileOutputStream out = new FileOutputStream(file);
		try {
			HttpEntity entity = this.response.getEntity();
			if (entity != null) {
				entity.writeTo(out);
			}
		} finally {
			this.consumed = true;
			out.close();
		}
	}

}