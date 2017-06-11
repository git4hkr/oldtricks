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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public abstract class Dom4jUtil {

	/**
	 * ストリームからDom4j Documentに変換します。
	 * 
	 * @param inputstream
	 * @return
	 * @throws DocumentException
	 */
	public static Document createDocumentFromStream(InputStream inputstream) throws DocumentException {
		return new SAXReader().read(inputstream);
	}

	/**
	 * XML FileからDom4j Documentに変換します。
	 * 
	 * @param url
	 * @return
	 * @throws DocumentException
	 */
	public static Document createDocumentFromFile(String url) throws DocumentException {
		return new SAXReader().read(url);
	}

	/**
	 * XML文字列からDom4j Documentに変換します。
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static Document createDocumentFromString(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml);
	}

	/**
	 * Dom4j DocumentをW3C Documentに変換します。
	 * 
	 * @param dom4jdoc
	 * @return
	 * @throws DocumentException
	 */
	public static org.w3c.dom.Document convertDom4jToW3c(org.dom4j.Document dom4jdoc) throws DocumentException {
		DOMWriter domWriter = new DOMWriter();
		return domWriter.write(dom4jdoc);
	}

	/**
	 * W3C DocumentをDom4j Documentに変換します。
	 * 
	 * @param w3cdoc
	 * @return
	 */
	public static org.dom4j.Document convertW3cToDom4j(org.w3c.dom.Document w3cdoc) {
		DOMReader xmlReader = new DOMReader();
		return xmlReader.read(w3cdoc);
	}

	/**
	 * Dom4j Documentをシリアライズし、String型で返却します。
	 * 
	 * @param dom4jdoc
	 * @param outputFormat
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String print(Document dom4jdoc, OutputFormat outputFormat) throws UnsupportedEncodingException,
			IOException {
		StringWriter swriter = new StringWriter();
		XMLWriter writer = new XMLWriter(swriter, outputFormat);
		writer.write(dom4jdoc);
		return swriter.toString();
	}

}
