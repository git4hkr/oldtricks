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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ファイルを監視し、差分を読み込みます。Tailの実装です。
 *
 * @author $Author: kubota $
 *
 */
public class TailfTask implements Runnable {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(TailfTask.class);

	/**
	 * {@link TailfTask} を取得します。取得したタスクは任意の{@link Executor}実装で実行できます。
	 *
	 * @param file
	 *            監視対象ファイル
	 * @param encoding
	 *            ファイルのエンコーディング
	 * @param handler
	 *            イベントハンドラ
	 * @param delay
	 *            監視周期（ms）
	 * @return
	 */
	public static TailfTask getInstance(File file, String encoding, TailfHandler handler, long delay) {
		return getInstance(file, encoding, handler, TimeUnit.MILLISECONDS, delay, false);
	}

	public static TailfTask getInstance(File file, String encoding, TailfHandler handler, TimeUnit timeUnit, long delay) {
		return getInstance(file, encoding, handler, timeUnit, delay, false);
	}

	public static TailfTask getInstance(File file, String encoding, TailfHandler handler, TimeUnit timeUnit,
			long delay, boolean end) {
		return new TailfTask(file, encoding, delay, timeUnit, handler, end);
	}

	/**
	 * 監視対象のファイル
	 */
	private File file;

	/** 文字コード */
	private String encoding = "UTF-8";

	/**
	 * 監視周期
	 */
	private long delay = 100;

	/**
	 * 監視周期の時間粒度
	 */
	private TimeUnit timeUnit;

	/**
	 * ファイルの終端から読み始める
	 */
	private boolean end;

	/**
	 * Tailfイベントハンドラー
	 */
	private TailfHandler handler;

	/**
	 * 監視継続フラグ。trueならば監視を継続します。
	 */
	private volatile boolean running = true;

	/** 最終監視時刻 */
	private long lastChackedTime = 0;

	/** ファイルカーソル */
	private long position = 0;

	private TailfTask(File file, String encoding, long delay, TimeUnit timeUnit, TailfHandler handler, boolean end) {
		super();
		this.file = file;
		this.encoding = encoding;
		this.delay = delay;
		this.timeUnit = timeUnit;
		this.handler = handler;
		this.end = end;
		this.handler.init(this);
	}

	/**
	 * 監視タスク
	 */
	public void run() {
		RandomAccessFile reader = null;
		try {
			/*
			 * ファイルのオープン。
			 */
			while (running && reader == null && isNeverInterrupted()) {
				try {
					reader = new RandomAccessFile(file, "r");
				} catch (FileNotFoundException e) {
					handler.fileNotFound(e);
				}

				if (reader == null) {
					waiting();
				} else {
					// ファイルカーソルの初期化
					setPosition(end ? file.length() : 0);
					updateLastChackedTime();
					reader.seek(position);
				}
			}

			LOG.info("starting tailf task.[{}]", file.getAbsoluteFile());
			/*
			 * 監視
			 */
			while (running && isNeverInterrupted()) {
				if (isRotate()) {
					handler.fileRotated();

					try {
						// Readerを再オープン
						RandomAccessFile save = reader;
						reader = new RandomAccessFile(file, "r");
						setPosition(0);
						// 次回GC時にクローズされる
						closeQuietly(save);
					} catch (FileNotFoundException e) {
						// 本ケースでは古いReaderとpositionを使い続ける
						handler.fileNotFound(e);
					}
					continue;
				} else {
					if (canReadAgain()) {
						// ファイルの読み込み
						updateLastChackedTime();
						setPosition(readLines(reader));
					} else if (isFileNewer()) {
						/**
						 * 本ケースはファイルがtruncateされるか、内容が同一サイズ のデータで上書きされた場合に発生します。
						 */
						// ファイルカーソルを先頭に戻す
						setPosition(0);
						reader.seek(position);
						// ファイルの読み込み
						updateLastChackedTime();
						setPosition(readLines(reader));
					}
				}
				waiting();
			}
		} catch (Exception e) {
			handler.handleException(e);
		} finally {
			closeQuietly(reader);
			LOG.info("finishing tailf task.[{}]", file.getAbsoluteFile());
		}
	}

	/**
	 * 指定時間waitします。
	 */
	void waiting() {
		try {
			timeUnit.sleep(delay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 最終監視時刻を更新します。
	 */
	void updateLastChackedTime() {
		lastChackedTime = System.currentTimeMillis();
	}

	/**
	 * 割り込み状態を取得します
	 *
	 * @return
	 */
	static boolean isNeverInterrupted() {
		return !Thread.currentThread().isInterrupted();
	}

	/**
	 * ファイルサイズを見てローテートされているかを確認します
	 *
	 * @return ローテートされている場合はTrue
	 */
	boolean isRotate() {
		long length = file.length();
		return length < position;
	}

	/**
	 * ファイルサイズを見てさらに読み込めるが確認します。
	 *
	 * @return 更に読み込める場合はTrue
	 */
	boolean canReadAgain() {
		long length = file.length();
		return length > position;
	}

	/**
	 * 監視タスクを停止します
	 */
	public void stop() {
		this.running = false;
		LOG.info("stopping tailf task.");
	}

	/**
	 * 複数行読み込み。
	 *
	 * @param reader
	 *            ファイルリーダー
	 * @return 読み込み後のファイルカーソル
	 * @throws Exception
	 */
	private long readLines(RandomAccessFile reader) throws Exception {
		long pos = reader.getFilePointer();
		String line = readLine(reader);
		while (line != null && isNeverInterrupted()) {
			pos = reader.getFilePointer();
			handler.handle(line);
			line = readLine(reader);
		}
		reader.seek(pos);
		return pos;
	}

	/**
	 * 1行読み込み。
	 *
	 * @param reader
	 *            ファイルリーダー
	 * @return 1行データ、または NULL（ファイル終端に達した場合）
	 * @throws IOException
	 */
	private String readLine(RandomAccessFile reader) throws IOException {
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		try {
			StringBuffer sb = new StringBuffer();
			int ch;
			boolean seenCR = false;
			while ((ch = reader.read()) >= 0) {
				switch (ch) {
				case '\n':
					sb.append(buff.toString(encoding));
					return sb.toString();
				case '\r':
					seenCR = true;
					break;
				default:
					if (seenCR) {
						sb.append('\r');
						seenCR = false;
					}
					buff.write(ch);
				}
			}
			return null;
		} finally {
			buff.close();
		}
	}

	/**
	 * ファイルが更新さてれいるか確認します
	 *
	 * @return
	 */
	public boolean isFileNewer() {
		if (file == null) {
			throw new IllegalArgumentException("No specified file");
		}
		if (!file.exists()) {
			return false;
		}
		if (file.lastModified() > lastChackedTime) {
			LOG.info("update file. lastModified()={}, lastChackedTime={}", file.lastModified(), lastChackedTime);
			return true;
		}
		return false;
	}

	/**
	 * ファイルをクローズします
	 *
	 * @param closeable
	 */
	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ignore) {
		}
	}

	/**
	 * 監視対象のファイルを取得します。
	 *
	 * @return 監視対象のファイル
	 */
	public File getFile() {
		return file;
	}

	/**
	 * 監視対象のファイルを設定します。
	 *
	 * @param file
	 *            監視対象のファイル
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * 監視周期を取得します。
	 *
	 * @return 監視周期
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * 監視周期を設定します。
	 *
	 * @param delay
	 *            監視周期
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * 監視周期の時間粒度を取得します。
	 *
	 * @return 監視周期の時間粒度
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * 監視周期の時間粒度を設定します。
	 *
	 * @param timeUnit
	 *            監視周期の時間粒度
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	/**
	 * ファイルの終端から読み始めるを取得します。
	 *
	 * @return ファイルの終端から読み始める
	 */
	public boolean isEnd() {
		return end;
	}

	/**
	 * ファイルの終端から読み始めるを設定します。
	 *
	 * @param end
	 *            ファイルの終端から読み始める
	 */
	public void setEnd(boolean end) {
		this.end = end;
	}

	/**
	 * Tailfイベントハンドラーを取得します。
	 *
	 * @return Tailfイベントハンドラー
	 */
	public TailfHandler getHandler() {
		return handler;
	}

	/**
	 * Tailfイベントハンドラーを設定します。
	 *
	 * @param handler
	 *            Tailfイベントハンドラー
	 */
	public void setHandler(TailfHandler handler) {
		this.handler = handler;
	}

	/**
	 * ファイルカーソルを取得します。
	 *
	 * @return ファイルカーソル
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * ファイルカーソルを設定します。
	 *
	 * @param position
	 *            ファイルカーソル
	 */
	public void setPosition(long position) {
		this.position = position;
	}

}
