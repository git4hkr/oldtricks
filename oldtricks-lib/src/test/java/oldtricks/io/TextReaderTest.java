package oldtricks.io;

import static org.junit.Assert.*;
import oldtricks.io.stream.LineConsumer;
import oldtricks.io.text.TextReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextReaderTest {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(TextReaderTest.class);

	@Test
	public void test() {
		try {
			TextReader.newInstance("pom.xml").forEach(new LineConsumer<String>() {

				@Override
				public void accept(String line, int lineNo) throws Exception {
					LOG.info(line);
				}
			}, TextReader.CLOSE_AND_RETHROW_POLICY);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
