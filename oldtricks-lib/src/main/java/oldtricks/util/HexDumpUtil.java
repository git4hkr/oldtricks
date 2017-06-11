/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package oldtricks.util;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * HEXダンプユーティリティです。
 *
 * @author kubota
 *
 */
public abstract class HexDumpUtil {
	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	/**
	 * Converts an array of bytes into an array of characters representing the
	 * hexadecimal values of each byte in order. The returned array will be
	 * double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @param toLowerCase
	 *            <code>true</code> converts to lowercase, <code>false</code> to
	 *            uppercase
	 * @return A char[] containing hexadecimal characters
	 * @since 1.4
	 */
	private static char[] byteToHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * Converts an array of bytes into an array of characters representing the
	 * hexadecimal values of each byte in order. The returned array will be
	 * double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @param toDigits
	 *            the output alphabet
	 * @return A char[] containing hexadecimal characters
	 * @since 1.4
	 */
	protected static char[] encodeHex(byte[] data, char[] toDigits) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	/**
	 * Converts an array of bytes into a String representing the hexadecimal
	 * values of each byte in order. The returned String will be double the
	 * length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @param data
	 *            a byte[] to convert to Hex characters
	 * @return A String containing hexadecimal characters
	 * @since 1.4
	 */
	public static String byteToHexString(byte[] data) {
		return new String(byteToHex(data, false));
	}

	/**
	 * byte列をコロン区切りのHEX文字列で返します。
	 *
	 * @param bytes
	 *            バイト列
	 * @return HEX文字列
	 */
	public static String hexDump(byte[] bytes) {
		return hexDump(bytes, ":").toString();
	}

	/**
	 * byte列を指定文字で区切り、HEX文字列で返します。
	 *
	 * @param bytes
	 *            バイト列
	 * @param delimiter
	 *            区切り文字
	 * @return HEX文字列
	 */
	public static StringBuilder hexDump(byte[] bytes, String delimiter) {
		final StringBuilder builder = new StringBuilder();
		char[] chars = byteToHex(bytes, true);
		for (char c : chars) {
			builder.append(c + delimiter);
		}
		if (StringUtil.isEmpty(delimiter))
			return builder;
		else
			return builder.deleteCharAt(builder.length() - delimiter.length());
	}

	/**
	 * デリミタ区切りのHEX文字列をbyte列に変換します。
	 *
	 * @param hex
	 *            HEX文字列
	 * @param delimiter
	 *            区切り文字
	 * @return byte列
	 */
	public static byte[] hexToBytes(String hex, String delimiter) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bytes;
		try {
			final String[] hexs = hex.split(delimiter);
			for (int i = 0; i < hexs.length; i++) {
				out.write(hexToByte(hexs[i]));
			}
			bytes = out.toByteArray();
		} finally {
			IOUtils.closeQuietly(out);
		}
		return bytes;
	}

	/**
	 * HEX文字列をbyte列に変換します。
	 *
	 * @param hex
	 *            HEX文字列
	 * @return byte列
	 */
	public static byte[] hexToBytes(String hex) {
		final byte[] out = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			out[i / 2] = hexToByte(hex.substring(i));
		}
		return out;
	}

	static final String HEX_VAL = "0123456789ABCDEF";

	/**
	 * HEX文字列をbyteに変換します。
	 *
	 * @param hex
	 *            HEX文字列
	 * @return byte値
	 */
	public static byte hexToByte(String hex) {
		final String uhex = hex.toUpperCase();
		if (hex.length() < 2) {
			throw new IllegalArgumentException("HEX must have at least 2 char. HEX:" + hex);
		} else {
			int high = HEX_VAL.indexOf(uhex.charAt(0));
			if (high < 0)
				throw new IllegalArgumentException("unknown hex char. :" + hex.charAt(0));
			int low = HEX_VAL.indexOf(uhex.charAt(1));
			if (low < 0)
				throw new IllegalArgumentException("unknown hex char. :" + hex.charAt(1));
			return (byte) ((high << 4) | low);
		}
	}
}
