package oldtricks.io.stream;

import oldtricks.io.Closeables;


/**
 * CSVファイルをクローズして、例外をリスローします。
 * 
 * @author $Author: kubota $
 * 
 */
public class CloseAndRethrowPolicy<T> implements ExceptionPolicy<T> {

	@Override
	public void apply(StreamTemplate<T> template, Exception e, T line) throws Exception {
		Closeables.closeQuietly(template);
		throw e;
	}

}
