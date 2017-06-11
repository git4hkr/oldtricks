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
package oldtricks.ex.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import oldtricks.bean.AbstractBean;

import org.apache.bval.jsr303.ApacheValidationProvider;
import org.apache.commons.lang3.StringUtils;

/**
 * JSR303 Validationを実装した抽象クラスです。validationしたいJavaBeanがこのクラスを継承することで、
 * アノテーションベースのvalidation機能を追加できます。
 *
 * @author $Author: kubota $
 *
 */
abstract public class Jsr303ValidationBean extends AbstractBean {
	private static final long serialVersionUID = 5822505864529570499L;

	public static class ValidateResult {
		private String message;
		private String path;
		private Object invalidValue;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public Object getInvalidValue() {
			return invalidValue;
		}

		public void setInvalidValue(Object invalidValue) {
			this.invalidValue = invalidValue;
		}

		@Override
		public String toString() {
			return path + "=[" + message + "]";
		}

	}

	/** Validator（シングルトン） */
	private static javax.validation.Validator validator;

	/**
	 * BeanのValidationを行います。
	 *
	 * @return validation結果のリスト
	 */
	public List<ValidateResult> validate() {
		getValidator();
		final ArrayList<ValidateResult> results = new ArrayList<Jsr303ValidationBean.ValidateResult>();

		final Set<ConstraintViolation<Jsr303ValidationBean>> violations = getValidator().validate(this);

		for (ConstraintViolation<Jsr303ValidationBean> violation : violations) {
			final ValidateResult result = new ValidateResult();
			result.setMessage(violation.getMessage());
			result.setPath(violation.getPropertyPath().toString());
			result.setInvalidValue(violation.getInvalidValue());
			results.add(result);
		}
		return results;
	}

	/**
	 * シングルトン{@link Validator} の実装を取得します。
	 *
	 * @return {@link Validator}
	 */
	synchronized static Validator getValidator() {
		if (validator == null) {
			ValidatorFactory factory = Validation.byProvider(ApacheValidationProvider.class).configure()
					.buildValidatorFactory();
			validator = factory.getValidator();
		}
		return validator;
	}

	/**
	 * validation結果がエラーの場合に{@link ValidationException}をスローします。<br>
	 *
	 * @throws ValidationException
	 *             ValidateResultのリストが1件以上のとき
	 */
	public void throwValidationExceptionIfNecessary() throws ValidationException {
		throwValidationExceptionIfNecessary(null);
	}

	/**
	 * validation結果がエラーの場合に{@link ValidationException}をスローします。<br>
	 *
	 * @param msg
	 *            メッセージ
	 *
	 * @throws ValidationException
	 *             ValidateResultのリストが1件以上のとき
	 */
	public void throwValidationExceptionIfNecessary(String msg) throws ValidationException {
		List<ValidateResult> results = validate();
		if (results != null && results.size() > 0) {
			String _msg = msg == null ? "" : msg + ": ";
			StringBuilder builder = new StringBuilder(_msg);
			List<String> messages = new ArrayList<String>();
			for (ValidateResult validateResult : results) {
				messages.add(validateResult.toString());
			}
			builder.append(StringUtils.join(messages.toArray(new String[0]), ", "));
			ValidationException ex = new ValidationException(builder.toString() + " : " + toString());
			throw ex;
		}
	}
}
