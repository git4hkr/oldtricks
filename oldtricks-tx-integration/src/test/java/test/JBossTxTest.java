package test;
import static org.junit.Assert.*;
import static org.springframework.transaction.TransactionDefinition.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JBossTxTest {
	@Value("#{jdbcTemplate1}")
	private JdbcTemplate xaJdbcTemplate1;
	@Value("#{jdbcTemplate2}")
	private JdbcTemplate xaJdbcTemplate2;
	@Value("#{transactionManager}")
	private PlatformTransactionManager transactionManager;

	/**
	 * PROPAGATION_REQUIRED を指定し、全トランザクションをXAでおこなう。
	 */
	@Test
	public void XAテスト() throws Exception {
		// テーブルの準備
		JdbcTestUtils.deleteFromTables(xaJdbcTemplate1, "sample");
		JdbcTestUtils.deleteFromTables(xaJdbcTemplate1, "sample_copy1");
		insertSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
		insertSample("sample_copy1", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
		final SampleVo beforeSample = selectSampleByPk("sample", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		final SampleVo beforeSampleCopy1 = selectSampleByPk("sample_copy1", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		TimeUnit.SECONDS.sleep(1);
		// テスト開始
		try {
			TransactionTemplate template = new TransactionTemplate(transactionManager);
			template.setPropagationBehavior(PROPAGATION_REQUIRED);
			template.setName("大外のトランザクション");
			template.afterPropertiesSet();
			template.execute(new TransactionCallback<Void>() {

				public Void doInTransaction(TransactionStatus status) {
					innodbLockWaitTimeout(xaJdbcTemplate1);
					deleteSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRED);
					insertSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRED);
					deleteSample("sample_copy1", 1, xaJdbcTemplate2, transactionManager, PROPAGATION_REQUIRED);
					insertSample("sample_copy1", 1, xaJdbcTemplate2, transactionManager, PROPAGATION_REQUIRED);
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 結果の確認
		final SampleVo afterSample = selectSampleByPk("sample", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		final SampleVo afterSampleCopy1 = selectSampleByPk("sample_copy1", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		System.out.println("beforeSample:" + beforeSample);
		System.out.println("afterSample :" + afterSample);
		assertFalse(beforeSample.equals(afterSample));
		System.out.println("beforeSampleCopy1:" + beforeSampleCopy1);
		System.out.println("afterSampleCopy1 :" + afterSampleCopy1);
		assertFalse(beforeSampleCopy1.equals(afterSampleCopy1));
	}

	/**
	 * PROPAGATION_REQUIRED
	 * を指定し、全トランザクションをXAでおこなう。その途中でSQL例外が発生した場合にロールバック範囲が全体である確認を行う。
	 */
	@Test
	public void XAロールバックテスト() throws Exception {
		// テーブルの準備
		JdbcTestUtils.deleteFromTables(xaJdbcTemplate1, "sample");
		JdbcTestUtils.deleteFromTables(xaJdbcTemplate1, "sample_copy1");
		insertSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
		insertSample("sample_copy1", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
		final SampleVo beforeSample = selectSampleByPk("sample", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		final SampleVo beforeSampleCopy1 = selectSampleByPk("sample_copy1", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		TimeUnit.SECONDS.sleep(1);
		// テスト開始
		try {
			TransactionTemplate template = new TransactionTemplate(transactionManager);
			template.setPropagationBehavior(PROPAGATION_REQUIRED);
			template.setName("大外のトランザクション");
			template.afterPropertiesSet();
			template.execute(new TransactionCallback<Void>() {

				public Void doInTransaction(TransactionStatus status) {
					innodbLockWaitTimeout(xaJdbcTemplate1);
					deleteSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRED);
					insertSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRED);
					insertSample("sample_copy1", 1, xaJdbcTemplate2, transactionManager, PROPAGATION_REQUIRED);// ここでduplicateKeyエラー
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 結果の確認
		final SampleVo afterSample = selectSampleByPk("sample", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		final SampleVo afterSampleCopy1 = selectSampleByPk("sample_copy1", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		System.out.println("beforeSample:" + beforeSample);
		System.out.println("afterSample :" + afterSample);
		assertTrue(beforeSample.equals(afterSample));
		System.out.println("beforeSampleCopy1:" + beforeSampleCopy1);
		System.out.println("afterSampleCopy1 :" + afterSampleCopy1);
		assertTrue(beforeSampleCopy1.equals(afterSampleCopy1));
	}

	/**
	 * PROPAGATION_REQUIRES_NEW
	 * を指定し、個別のトランザクションをXAでおこなう。その途中でSQL例外が発生した場合にロールバック範囲は限定的である確認を行う。
	 *
	 * @throws InterruptedException
	 * @throws Exception
	 */
	@Test
	public void XA個別トランザクションRollbackテスト() throws InterruptedException {
		// テーブルの準備
		JdbcTestUtils.deleteFromTables(xaJdbcTemplate1, "sample");
		JdbcTestUtils.deleteFromTables(xaJdbcTemplate1, "sample_copy1");
		insertSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
		insertSample("sample_copy1", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
		final SampleVo beforeSample = selectSampleByPk("sample", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		final SampleVo beforeSampleCopy1 = selectSampleByPk("sample_copy1", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		TimeUnit.SECONDS.sleep(1);
		// テスト開始
		try {
			TransactionTemplate template = new TransactionTemplate(transactionManager);
			template.setPropagationBehavior(PROPAGATION_REQUIRED);
			template.setName("大外のトランザクション");
			template.afterPropertiesSet();
			template.execute(new TransactionCallback<Void>() {

				public Void doInTransaction(TransactionStatus status) {
					innodbLockWaitTimeout(xaJdbcTemplate1);
					deleteSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
					insertSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRES_NEW);
					insertSample("sample_copy1", 1, xaJdbcTemplate2, transactionManager, PROPAGATION_REQUIRES_NEW);// ここでduplicateKeyエラー
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		final SampleVo afterSample = selectSampleByPk("sample", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		final SampleVo afterSampleCopy1 = selectSampleByPk("sample_copy1", 1, xaJdbcTemplate1, transactionManager,
				PROPAGATION_REQUIRED);
		System.out.println("beforeSample:" + beforeSample);
		System.out.println("afterSample :" + afterSample);
		assertFalse(beforeSample.equals(afterSample));
		System.out.println("beforeSampleCopy1:" + beforeSampleCopy1);
		System.out.println("afterSampleCopy1 :" + afterSampleCopy1);
		assertEquals(beforeSampleCopy1, afterSampleCopy1);
	}

	/**
	 * [目視確認]<BR>
	 * 異なるデータソースで同一のテーブルのレコードにXAアクセスすると、 異なるコネクションでアクセス後は同一のコネクションでもデッドロックが発生する。
	 * テーブルロックのような状態になるため、別行の更新もブロックされる。
	 *
	 * @throws Exception
	 */
	@Test
	public void XAデッドロックタイムアウト() throws Exception {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.setPropagationBehavior(PROPAGATION_REQUIRED);
		template.setName("大外のトランザクション");
		template.afterPropertiesSet();
		template.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(TransactionStatus status) {
				innodbLockWaitTimeout(xaJdbcTemplate1);
				deleteSample("sample", 1, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRED);
				deleteSample("sample", 2, xaJdbcTemplate1, transactionManager, PROPAGATION_REQUIRED);// この行がなければうまくいく.
				insertSample("sample", 1, xaJdbcTemplate2, transactionManager, PROPAGATION_REQUIRED);// デッドロック
				return null;
			}
		});
	}

	public static void deleteSample(final String table, final int id, final JdbcTemplate jdbcTemplate,
			PlatformTransactionManager tm, int propagation) {
		TransactionTemplate template = new TransactionTemplate(tm);
		template.setPropagationBehavior(propagation);
		template.setName("deleteのトランザクション");
		template.afterPropertiesSet();
		template.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(TransactionStatus status) {
				final String query = new SQL() {
					{
						DELETE_FROM(table);
						WHERE("id = ?");
					}
				}.toString();
				Object[] args = new Object[] { id };
				final int cnt = jdbcTemplate.update(query, args);
				System.out.println(cnt);
				if (false)
					throw new RuntimeException();
				return null;
			}
		});
	}

	public static void insertSample(final String table, final int id, final JdbcTemplate jdbcTemplate,
			PlatformTransactionManager tm, int propagation) {
		TransactionTemplate template = new TransactionTemplate(tm);
		template.setPropagationBehavior(propagation);
		template.setName("insertのトランザクション");
		template.afterPropertiesSet();
		template.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(TransactionStatus status) {
				final String query = new SQL() {
					{
						INSERT_INTO(table);
						VALUES("id", "?");
					}
				}.toString();
				Object[] args = new Object[] { id };
				final int cnt = jdbcTemplate.update(query, args);
				System.out.println(cnt);
				if (false)
					throw new RuntimeException();
				return null;
			}
		});
	}

	public static SampleVo selectSampleByPk(final String table, final int id, final JdbcTemplate jdbcTemplate,
			PlatformTransactionManager tm, int propagation) {
		TransactionTemplate template = new TransactionTemplate(tm);
		template.setPropagationBehavior(propagation);
		template.setName("selectのトランザクション");
		template.setReadOnly(true);
		template.afterPropertiesSet();
		List<SampleVo> list = template.execute(new TransactionCallback<List<SampleVo>>() {
			public List<SampleVo> doInTransaction(TransactionStatus status) {
				final String query = new SQL() {
					{
						SELECT("*");
						FROM(table);
						WHERE("id = ?");
					}
				}.toString();
				return jdbcTemplate.query(query, new Object[] { id }, new BeanPropertyRowMapper<SampleVo>(
						SampleVo.class));
			}
		});
		return list.size() == 0 ? null : list.get(0);
	}

	public static void innodbLockWaitTimeout(JdbcTemplate jdbcTemplate) {
		final String query = new SQL() {
			{
				SELECT("@@innodb_lock_wait_timeout");
			}
		}.toString();
		final int timeout = jdbcTemplate.queryForObject(query,Integer.class);
		System.out.println("innodb_lock_wait_timeout: " + timeout);
	}

}
