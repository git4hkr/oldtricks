package oldtricks.util;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.DigestUtils;

public abstract class EncriptUtil {

	public static AesUtil genAes(String password) {
		return new AesUtil(password);
	}

	public static class AesUtil {
		private byte[] encryptKey;
		private byte[] encryptIv;

		public AesUtil(String password) {
			super();
			init(password);
		}

		/**
		 * パスワードから秘密鍵と初期ベクトルを生成します。 opensslコマンド互換の生成方式です。
		 *
		 * @param password
		 */
		private void init(String password) {
			byte[] pass = password.getBytes();
			Assert.isTrue(pass.length == 16, "password length must be 16byte.");
			encryptKey = DigestUtils.md5Digest(pass);
			byte[] ivBytes = new byte[32];
			Arrays.fill(ivBytes, (byte) 0);
			for (int i = 0; i < 16; i++) {
				ivBytes[i] = encryptKey[i];
			}
			for (int i = 0; i < 16; i++) {
				ivBytes[i + 16] = pass[i];
			}
			encryptIv = DigestUtils.md5Digest(ivBytes);
		}

		/**
		 * AES暗号化メソッドです。以下のコマンドと同じ結果となります。 <br>
		 *
		 * <pre>
		 * 例）文字列（aaaaaaa）をパスワード（1234567890123456）で暗号化
		 * echo -n "aaaaaaa" |base64 | openssl enc -aes-128-cbc -e -pass pass:1234567890123456 -nosalt
		 * </pre>
		 *
		 * @param text
		 *            暗号化する文字列
		 * @return 暗号化文字列
		 * @throws GeneralSecurityException
		 */
		public byte[] encrypt(byte[] byteText) throws GeneralSecurityException {
			SecretKeySpec key = new SecretKeySpec(encryptKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(encryptIv);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			byte[] byteResult = cipher.doFinal(byteText);
			// 暗号化文字列を返却
			return byteResult;
		}

		/**
		 * 復号化メソッド
		 *
		 * @param text
		 *            復号化する文字列
		 * @return 復号化文字列
		 */
		public byte[] decrypt(byte[] byteText) throws GeneralSecurityException {
			SecretKeySpec key = new SecretKeySpec(encryptKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(encryptIv);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] byteResult = cipher.doFinal(byteText);
			return byteResult;
		}
	}
}
