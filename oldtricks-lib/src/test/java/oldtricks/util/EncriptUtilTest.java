package oldtricks.util;

import static org.junit.Assert.fail;

import java.security.GeneralSecurityException;

import org.junit.Test;

public class EncriptUtilTest {

	@Test
	public void test() {
		try {
			EncriptUtil.genAes("1234567890123456").encrypt("a".getBytes());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			fail();
		}
	}

}
