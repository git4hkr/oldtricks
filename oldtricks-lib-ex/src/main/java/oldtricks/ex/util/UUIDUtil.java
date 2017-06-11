package oldtricks.ex.util;

import java.util.UUID;

import com.fasterxml.uuid.Generators;

abstract public class UUIDUtil {

	/**
	 * version1 UUID文字列を返却します。
	 *
	 * @return UUID文字列
	 */
	public static String getVersion1UUID() {
		return timeBasedUUID().toString();
	}

	/**
	 * ランダム数をキーにversion1のUUIDを生成します。
	 *
	 * @return UUID
	 */
	public static UUID randomBasedUUID() {
		return Generators.randomBasedGenerator().generate();
	}

	/**
	 * 時刻をキーにversion1のUUIDを生成します。
	 *
	 * @return UUID
	 */
	public static UUID timeBasedUUID() {
		return Generators.timeBasedGenerator().generate();
	}

}
