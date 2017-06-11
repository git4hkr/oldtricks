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
package oldtricks.tool.tail;

import java.io.FileNotFoundException;

/**
 * {@link TailfTask}のイベントハンドラーです。
 *
 *
 * @author $Author: kubota $
 *
 */
public interface TailfHandler {
	/**
	 * 初期化時に呼び出されます。
	 *
	 * @param task
	 */
	void init(TailfTask task);

	/**
	 * 監視ファイルが見つからなかった場合に呼び出されます
	 *
	 * @throws Exception
	 */
	void fileNotFound(FileNotFoundException e) throws Exception;

	/**
	 * 監視ファイルのローテートを検出した時、またはファイルの再オープン前に呼び出されます。再オープンに失敗した場合、
	 * {@link #fileNotFound(FileNotFoundException)}が呼び出されます。
	 */
	void fileRotated() throws Exception;

	/**
	 * 1行読み込んだ時に呼び出されます。
	 *
	 * @param line
	 *            1行データ
	 * @throws Exception
	 */
	void handle(String line) throws Exception;

	/**
	 * 例外発生時に呼び出されます。
	 *
	 * @param ex
	 */
	void handleException(Exception ex);
}
