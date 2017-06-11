package sample;

import oldtricks.httpclient.Executor;
import oldtricks.httpclient.HttpClientExecutorFactory;
import oldtricks.httpclient.Request;
import oldtricks.httpclient.Response;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class Sample {
	private static final String GOOGLE_LOGIN_URL = "https://www.google.com/accounts/ClientLogin";

	@Test
	public void googleAuthApi() throws Exception {
		final Executor executor = HttpClientExecutorFactory.builder().socketTO(10000).connectTO(1000).build();
		try {
			final String accountType = "GOOGLE";
			final String email = "xxxxxxxxx@gmail.com";
			final String passwd = "**********";
			final String source = "fromtestcode";
			final String service = "cl";
			GoogleAuthForm authForm = new GoogleAuthForm(accountType, email, passwd, source, service);
			final Request req = Request.Post(GOOGLE_LOGIN_URL).bodyString(authForm.toString(),
					ContentType.APPLICATION_FORM_URLENCODED);
			Response res = executor.execute(req);
			if (res.status() == 200) {
				System.out.println(EntityUtils.toString(res.returnResponse().getEntity()));
				for (Header header : res.returnResponse().getAllHeaders()) {
					System.out.println(header.getName() + ":" + header.getValue());
				}
			} else
				System.out.println("status:" + res.status());
		} finally {
			executor.shutdown();
		}
	}

	public static class GoogleAuthForm {
		private String accountType = "GOOGLE";
		private String email = "xxxxxx@gmail.com";
		private String passwd = "*******";
		private String source = "fromtestcode";
		private String service = "ac2dm";

		public GoogleAuthForm(String accountType, String email, String passwd, String source, String service) {
			super();
			this.accountType = accountType;
			this.email = email;
			this.passwd = passwd;
			this.source = source;
			this.service = service;
		}

		public String getAccountType() {
			return accountType;
		}

		public String getEmail() {
			return email;
		}

		public String getPasswd() {
			return passwd;
		}

		public String getSource() {
			return source;
		}

		public String getService() {
			return service;
		}

		@Override
		public String toString() {
			return "accountType=" + accountType + "&Email=" + email + "&Passwd=" + passwd + "&source=" + source
					+ "&service=" + service;
		}

	}
}
