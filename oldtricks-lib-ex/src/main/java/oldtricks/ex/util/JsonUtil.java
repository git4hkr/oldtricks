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
package oldtricks.ex.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonUtil {
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * オブジェクトをJSONにシリアライズします。
	 *
	 * @param bean
	 *            シリアライズするオブジェクト
	 * @return シリアライズされた文字列
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String serialize(Object bean) throws JsonGenerationException, JsonMappingException, IOException {
		return mapper.writeValueAsString(bean);
	}

	/**
	 * オブジェクトをJSONにシリアライズします。
	 *
	 * @param outputStream
	 *            出力先ストリーム
	 * @param charsetName
	 *            出力時の文字エンコーディング
	 * @param bean
	 *            シリアライズするオブジェクト
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static void serialize(OutputStream outputStream, String charsetName, Object bean)
			throws JsonGenerationException, JsonMappingException, IOException {
		serialize(new OutputStreamWriter(outputStream, charsetName), bean);
	}

	/**
	 * オブジェクトをJSONにシリアライズします。
	 *
	 * @param writer
	 *            出力先のライター
	 * @param bean
	 *            シリアライズするオブジェクト
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static void serialize(Writer writer, Object bean) throws JsonGenerationException, JsonMappingException,
			IOException {
		mapper.writeValue(writer, bean);
	}

	/**
	 * JSONをオブジェクトにデシリアライズします。
	 *
	 * @param content
	 *            JSON文字列
	 * @param valueType
	 *            デシリアライズされたオブジェクト
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T deserialize(String content, Class<T> valueType) throws JsonParseException,
			JsonMappingException, IOException {
		return mapper.readValue(content, valueType);
	}

	/**
	 * JSONをオブジェクトにデシリアライズします。
	 *
	 * @param inputStream
	 *            入力するストリーム
	 * @param charsetName
	 *            週力時の文字エンコーディング
	 * @param valueType
	 *            デシリアライズされたオブジェクト
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T deserialize(InputStream inputStream, String charsetName, Class<T> valueType)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(new InputStreamReader(inputStream, charsetName), valueType);
	}

	/**
	 * JSONをオブジェクトにデシリアライズします。
	 *
	 * @param reader
	 *            入力するリーダー
	 * @param valueType
	 *            デシリアライズされたオブジェクト
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T deserialize(Reader reader, Class<T> valueType) throws JsonParseException, JsonMappingException,
			IOException {
		return mapper.readValue(reader, valueType);
	}
}
