package oldtricks.ex.util;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonUtilTest {

	@Test
	public void test() throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println(JsonUtil.serialize(new TestBean()));
		fail("まだ実装されていません");
	}

	public static class TestBean {
		private String name = "kubota";
		private String mail = "kubota@mail.com";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}
	}
}
