package oldtricks.io.text;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import oldtricks.io.stream.CloseAndRethrowPolicy;
import oldtricks.io.stream.ExceptionPolicy;
import oldtricks.io.stream.StreamTemplate;

public class TextReader extends StreamTemplate<String> {
	/** 例外が発生した場合、ファイルをクローズして、発生した例外をリスローします。 */
	public static final ExceptionPolicy<String> DEFAULT_POLICY = new CloseAndRethrowPolicy<>();
	/** 例外が発生した場合、ファイルをクローズして、発生した例外をリスローします。 */
	public static final ExceptionPolicy<String> CLOSE_AND_RETHROW_POLICY = new CloseAndRethrowPolicy<>();
	private LineIterator ite;

	public static TextReader newInstance(Reader reader) {
		return new TextReader(reader);
	}

	public static TextReader newInstance(String textFile) throws IOException {
		return newInstance(textFile, "UTF-8");
	}

	public static TextReader newInstance(String textFile, String encoding) throws IOException {
		InputStream inputStream = new FileInputStream(textFile);
		return new TextReader(inputStream, encoding);
	}

	public TextReader(Reader reader) {
		super();
		ite = IOUtils.lineIterator(reader);
	}

	public TextReader(InputStream stream, String encoding) throws IOException {
		super();
		ite = IOUtils.lineIterator(stream, encoding);
	}

	@Override
	public void close() throws IOException {
		ite.close();
	}

	@Override
	protected String readNext() throws IOException {
		return ite.hasNext() ? ite.nextLine() : null;
	}

	@Override
	protected boolean hasNextLine() throws IOException {
		return ite.hasNext();
	}

}
