import java.io.IOException;
import java.net.InetSocketAddress;

import net.spy.memcached.MemcachedClient; // This is the AWS-provided library with Auto Discovery support

import org.junit.Test;

public class MemcachedTest {

	public static void main(String[] args) throws IOException {

	}

	@Test
	public void test() {
		try {
			String configEndpoint = "localhost";
			Integer clusterPort = 11211;

			MemcachedClient client = new MemcachedClient(new InetSocketAddress(configEndpoint, clusterPort));
			System.out.println(client.get("AA"));
			// client.set("AA", 10, "AAAAAAAAAA").get();
			client.shutdown();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
