package oldtricks.io.csv;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import oldtricks.io.stream.LineConsumer;
import oldtricks.util.StringUtil;

public class CSVReaderTest {

	@Test
	public void 正常系() {
		try {
			CSVReader.newInstance("src/test/resources/message.csv").forEach(new LineConsumer<String[]>() {

				@Override
				public void accept(String[] line, int lineNo) throws Exception {
					System.out.println(ArrayUtils.toString(line));
				}
			}, CSVReader.CLOSE_AND_RETHROW_POLICY);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void 異常系() {
		final Exception ex = new IllegalStateException("異常系テスト");
		try {
			CSVReader.newInstance("src/test/resources/message.csv").forEach(new LineConsumer<String[]>() {

				@Override
				public void accept(String[] line, int lineNo) throws Exception {
					throw ex;
				}
			}, CSVReader.CLOSE_AND_RETHROW_POLICY);
			fail();
		} catch (Exception e) {
			assertSame(ex, e);
		}

	}

	@Test
	public void Java8StremAPI() {
		try (CSVReader csv = CSVReader.newInstance("src/test/resources/message.csv")) {
			Map<String, List<String>> join = csv.lines()
					.map(StringUtil::join)
					.filter(StringUtil::isNotBlank)
					.collect(Collectors.groupingBy(v -> v));
			System.out.println(join);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
