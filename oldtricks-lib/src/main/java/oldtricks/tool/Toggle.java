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
package oldtricks.tool;

/**
 * オブジェクトのトグルです。
 *
 * @author $Author: kubota $
 *
 */
public class Toggle<T> {
	/** 値のリスト */
	private T[] values;
	/** リストの現在位置 */
	private int cursor;
	/** しきい値 */
	private int threshold;
	/** {@link #toggled()}の呼び出し回数 */
	private int counter;

	/**
	 * コンストラクタ。
	 *
	 * @param threshold
	 *            値を変えるまでのしきい値です。{@link #toggled()}をしきい値回呼び出すと次の値に切り替わります。
	 * @param values
	 *            値のリストです。
	 */
	@SafeVarargs
	public Toggle(int threshold, T... values) {
		super();
		this.threshold = threshold;
		this.values = values;
	}

	/**
	 * コンストラクタで与えられた値のリストを次値に切り替えます。
	 *
	 * @return トグルオブジェクト
	 */
	public Toggle<T> toggled() {
		counter++;
		if (counter >= threshold) {
			counter = 0;
			cursor++;
			if (cursor >= values.length)
				cursor = 0;
		}
		return this;
	}

	/**
	 * 現在の値を取得します。
	 */
	public T get() {
		if (values == null || values.length < 1)
			return null;
		return values[cursor];
	}

	/**
	 * 現在の値のStringを取得します。
	 */
	@Override
	public String toString() {
		if (values == null || values.length < 1)
			return "";
		return values[cursor].toString();
	}
}
