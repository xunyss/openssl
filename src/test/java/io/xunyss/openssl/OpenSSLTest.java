package io.xunyss.openssl;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 *
 * @author XUNYSS
 */
public class OpenSSLTest {
	
	@Test
	public void exec() throws IOException {
		OpenSSL openssl = new OpenSSL();
		openssl.exec("version");

		System.out.println(openssl.getOutput());

//		Assert.assertEquals("OpenSSL 1.0.2g  1 Mar 2016" + "\r\n", openssl.getOutput());
	}
}
