package oldtricks.io.stream;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import oldtricks.io.Closeables;
import oldtricks.util.Assert;

public abstract class StreamTemplate<T> implements Closeable {
	/**
	 * テキスト形式のファイルを行ごとに処理します。正常終了時にはファイルをクローズしてから復帰します。
	 *
	 * @param consumer
	 *            処理するプログラム。NULL不可。
	 * @param policy
	 *            例外発生時のリアクションポリシー。NULL可。
	 * @throws Exception
	 *             処理中に発生した例外
	 */
	public void forEach(LineConsumer<T> consumer, ExceptionPolicy<T> policy) throws Exception {
		forEach(consumer, policy, true);
	}

	/**
	 * テキスト形式のファイルを行ごとに処理します。
	 *
	 * @param consumer
	 *            処理するプログラム。NULL不可。
	 * @param policy
	 *            例外発生時のリアクションポリシー。NULL可。
	 * @param withClose
	 *            trueならば終了時にファイルをクローズする。
	 * @throws Exception
	 *             処理中に発生した例外
	 */
	public void forEach(LineConsumer<T> consumer, ExceptionPolicy<T> policy, boolean withClose) throws Exception {
		Assert.notNull(consumer, "consumer must not be null.");
		try {
			T line;
			int lineNo = 1;
			while ((line = readNext()) != null) {
				try {
					consumer.accept(line, lineNo);
				} catch (Exception e) {
					if (policy != null)
						policy.apply(this, e, line);
				} finally {
					lineNo++;
				}
			}
		} finally {
			if (withClose)
				Closeables.closeQuietly(this);
		}
	}

	/**
	 * テキストファイルの次の行を取得します。ファイルの終端に到達した場合nullを返却します。
	 *
	 * @return
	 * @throws IOException
	 */
	protected abstract T readNext() throws IOException;

	protected abstract boolean hasNextLine() throws IOException;

	public Stream<T> lines() {
		Iterator<T> iter = new Iterator<T>() {

			@Override
			public boolean hasNext() {
				try {
					return hasNextLine();
				} catch (IOException e) {
					new RuntimeException(e);
				}
				return false;
			}

			@Override
			public T next() {
				try {
					return readNext();
				} catch (IOException e) {
					new RuntimeException(e);
				}
				return null;
			}
		};
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
	}
}
