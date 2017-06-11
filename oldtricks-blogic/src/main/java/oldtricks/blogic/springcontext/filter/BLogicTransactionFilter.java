package oldtricks.blogic.springcontext.filter;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import oldtricks.blogic.BLogicFunction;
import oldtricks.blogic.BLogicTransaction;
import oldtricks.blogic.springcontext.BLogicFilterWithLifeCycle;

/**
 * {@link BatchBLogic} の {@link BLogicFunction}アノテーションが付与されたメソッドに挿入される共通処理です。
 *
 * @author $Author$
 *
 */
public class BLogicTransactionFilter implements BLogicFilterWithLifeCycle {

	public final class ExceptionTransporter extends RuntimeException {
		private static final long serialVersionUID = -973727528239072954L;

		public ExceptionTransporter(Throwable cause) {
			super(cause);
		}

	}

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(BLogicTransactionFilter.class);
	private ApplicationContext context;

	@Override
	public Object intercept(Object target, Method method, Object[] args, Next next) throws Throwable {
		LOG.trace("invoke BLogic method with BLogicTransactionFilter.[{}]", method);

		try {
			BLogicTransaction transaction = getTransaction(context, method);
			PlatformTransactionManager transactionManager = getTransactionManager(context, transaction, method);
			TransactionTemplate template = new TransactionTemplate(transactionManager);
			template.setIsolationLevel(transaction.isolationLevel());
			template.setPropagationBehavior(transaction.propagationBehavior());
			template.setReadOnly(transaction.readOnly());
			template.setTimeout(transaction.timeout());
			template.afterPropertiesSet();

			return template.execute((status) -> {
				try {
					return next.invoke(target, method, args);
				} catch (Throwable e) {
					throw new ExceptionTransporter(e);
				}
			});
		} catch (ExceptionTransporter transporter) {
			throw transporter.getCause();
		}
	}

	/**
	 * トランザクションマネージャーを取得します。
	 *
	 * @param context
	 *            Springコンテキスト
	 * @param transaction
	 *            トランザクションアノテーション
	 * @return トランザクションマネージャー
	 */
	protected PlatformTransactionManager getTransactionManager(ApplicationContext context,
			BLogicTransaction transaction, Method method) {
		PlatformTransactionManager transactionManager = null;
		transactionManager = (PlatformTransactionManager) context.getBean(transaction.txManegerName());
		return transactionManager;
	}

	/**
	 * トランザクションアノテーションを取得します。
	 *
	 * @param context
	 *            Springコンテキスト
	 * @param method
	 *            インターセプター対象メソッド
	 * @return トランザクションマネージャー
	 */
	protected BLogicTransaction getTransaction(ApplicationContext context, Method method) {
		BLogicTransaction transaction = method.getAnnotation(BLogicTransaction.class);
		return transaction;
	}

	@Override
	public boolean accept(Method method) {
		try {
			return method.getAnnotation(BLogicTransaction.class) != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void init(Object target, ApplicationContext applicationContext) throws Throwable {
		context = applicationContext;
	}

	@Override
	public void destory() throws Throwable {

	}

}
