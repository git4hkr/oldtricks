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
package oldtricks.lock;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;

import oldtricks.io.Closeables;

/**
 * プロセス間の排他ロックのテンプレートを提供します。
 *
 * @author ${Author}
 *
 */
public class InterProcLockTemplate {

	/**
	 * {@link FileLock} を利用したプロセス間排他のテンプレートメソッドです。
	 *
	 * @param lockfile
	 *            ロックファイル
	 * @param callback
	 *            排他区間の処理の実装
	 * @param param
	 *            パラメータ
	 * @param <T>
	 *            パラメータタイプ
	 * @throws LockFailedException
	 *             ロックの取得に失敗した場合
	 * @throws Exception
	 *             IOエラー時およびコールバックメソッド内で発生した例外
	 */
	public static <T> void process(File lockfile, ExclusiveCallback<T> callback, T param) throws LockFailedException,
			Exception {
		FileOutputStream os = new FileOutputStream(lockfile);
		try {
			FileLock lock = os.getChannel().tryLock();
			if (lock == null)
				throw new LockFailedException("failed to obtain lock on file [" + lockfile.getAbsolutePath() + "]");
			try {
				callback.exclusiveProcess(param);
			} finally {
				lock.release();
			}
		} finally {
			Closeables.closeQuietly(os);
		}
	}

	public static class LockFailedException extends Exception {
		private static final long serialVersionUID = -59730822193953111L;

		/**
		 * デフォルトコンストラクタ
		 */
		public LockFailedException() {
			super();
		}

		/**
		 * コンストラクタ。
		 *
		 * @param message
		 *            メッセージ
		 * @param cause
		 *            原因
		 * @param enableSuppression
		 * @param writableStackTrace
		 */
		public LockFailedException(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		/**
		 *
		 * @param message
		 * @param cause
		 */
		public LockFailedException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 *
		 * @param message
		 */
		public LockFailedException(String message) {
			super(message);
		}

		public LockFailedException(Throwable cause) {
			super(cause);
		}
	}

}
