package test;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oldtricks.ex.util.DateTimeUtil;
import oldtricks.io.csv.CSVReader;
import oldtricks.util.StringUtils;

import org.apache.ibatis.jdbc.SQL;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TxIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(TxIntegrationTest.class);
	@Rule
	public TestName name = new TestName();

	@Value("#{jdbcTemplate1}")
	private JdbcTemplate jdbcTemplate;

	@Value("#{jdbcTemplate1}")
	private JdbcTemplate xaJdbcTemplate1;
	@Value("#{jdbcTemplate2}")
	private JdbcTemplate xaJdbcTemplate2;

	@Value("#{txPerform}")
	private XaTxPerformer txPerform;

	@Value("#{transactionManager}")
	private PlatformTransactionManager jbosstransactionManager;

	@Test
	public void XAテスト() throws Exception {
		TransactionTemplate template = new TransactionTemplate(jbosstransactionManager);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		template.setName("大外のトランザクション");
		template.afterPropertiesSet();
		template.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(TransactionStatus status) {
				innodbLockWaitTimeout();
				delete(1, xaJdbcTemplate1, jbosstransactionManager);
				// delete(2, xaJdbcTemplate2, jbosstransactionManager);
				insert(1, xaJdbcTemplate1, jbosstransactionManager);
				// insert(2, xaJdbcTemplate2, jbosstransactionManager);
				return null;
			}
		});
	}

	@Test
	public void XAデッドロックタイムアウト() throws Exception {
		TransactionTemplate template = new TransactionTemplate(jbosstransactionManager);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		template.setName("大外のトランザクション");
		template.afterPropertiesSet();
		template.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(TransactionStatus status) {
				innodbLockWaitTimeout();
				delete(1, xaJdbcTemplate1, jbosstransactionManager);
				delete(2, xaJdbcTemplate1, jbosstransactionManager);
				insert(1, xaJdbcTemplate2, jbosstransactionManager);// デッドロック
				insert(2, xaJdbcTemplate2, jbosstransactionManager);
				return null;
			}
		});
	}

	public static void delete(final int id, final JdbcTemplate jdbcTemplate, PlatformTransactionManager tm) {
		TransactionTemplate template = new TransactionTemplate(tm);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		template.setName("deleteのトランザクション");
		template.afterPropertiesSet();
		template.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(TransactionStatus status) {
				final String query = new SQL() {
					{
						DELETE_FROM("sample");
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

	public static void insert(final int id, final JdbcTemplate jdbcTemplate, PlatformTransactionManager tm) {
		TransactionTemplate template = new TransactionTemplate(tm);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		template.setName("insertのトランザクション");
		template.afterPropertiesSet();
		template.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(TransactionStatus status) {
				final String query = new SQL() {
					{
						INSERT_INTO("sample");

					}
				}.toString() + " VALUES(?,?,?,?)";
				Object[] args = new Object[] { id, "name", DateTimeUtil.now().toDate(), DateTimeUtil.now().toDate() };
				final int cnt = jdbcTemplate.update(query, args);
				System.out.println(cnt);
				if (false)
					throw new RuntimeException();
				return null;
			}
		});
	}

	@Test
	public void innodbLockWaitTimeout() {
		final String query = new SQL() {
			{
				SELECT("@@innodb_lock_wait_timeout");
			}
		}.toString();
		final int timeout = jdbcTemplate.queryForObject(query,Integer.class);
		System.out.println("innodb_lock_wait_timeout: " + timeout);
	}

	@Test
	public void isAutocommit() throws Exception {
		final String query = new SQL() {
			{
				SELECT("@@autocommit");
			}
		}.toString();

		final boolean autocimmit = jdbcTemplate.queryForObject(query,Integer.class) == 1;
		System.out.println(autocimmit);
	}

	@Test
	@Rollback(true)
	public void 試験データの投入() {
		StopWatch stopWatch = new StopWatch();
		try {
			int n = JdbcTestUtils.deleteFromTables(jdbcTemplate, "t_timeline");
			LOG.info("deleted rows :{}", n);
			n = JdbcTestUtils.deleteFromTables(jdbcTemplate, "t_content");
			LOG.info("deleted rows :{}", n);
			final int X = 800;
			final int Y = 31;
			final int Z = 2;
			String queryTimeline = new SQL() {
				{
					INSERT_INTO("t_timeline");
					VALUES("timeline_id", "?");
					VALUES("content_id", "?");
					VALUES("rflct_ex_date", "?");
					VALUES("cre_date", "?");
				}
			}.toString();

			String queryContent = new SQL() {
				{
					INSERT_INTO("t_content");
					VALUES("content_id", "?");
					VALUES("stop_flg", "?");
					VALUES("cre_date", "?");
					VALUES("rflct_ex_date", "?");
					VALUES("end_ex_date", "?");
				}
			}.toString();
			stopWatch.start();
			int cnt = 1;
			final List<Object[]> contentList = new ArrayList<Object[]>();
			final List<Object[]> timelineList = new ArrayList<Object[]>();
			for (int i = 0; i < X; i++) {
				try {
					final String contentId = String.format("%010d", i);
					final String stopFlg = "" + i % 2;
					final DateTime dt = new DateTime().minusDays(i);
					final Timestamp creDate = new Timestamp(dt.getMillis());
					final String rflctExDate = dt.toString("YYYYMMdd");
					final String endExDate = "99991231";
					final Object[] params = new Object[] { contentId, stopFlg, creDate, rflctExDate, endExDate };
					contentList.add(params);
					// LOG.info("{}",
					// ToStringBuilder.reflectionToString(params));
				} finally {
				}
				for (int j = 0; j < Y; j++) {
					for (int k = 0; k < Z; k++, cnt++) {
						final String timelineId = String.format("%011d", cnt);
						final String contentId = String.format("%010d", i);
						final DateTime dt = new DateTime().minusDays(j);
						final Timestamp creDate = new Timestamp(dt.getMillis());
						final String rflctExDate = dt.toString("YYYYMMddHH");
						final Object[] params = new Object[] { timelineId, contentId, rflctExDate, creDate };
						timelineList.add(params);
						// LOG.info("{}",
						// ToStringBuilder.reflectionToString(params));
					}
				}
			}
			jdbcTemplate.batchUpdate(queryTimeline, timelineList);
			jdbcTemplate.batchUpdate(queryContent, contentList);
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			stopWatch.stop();
			LOG.info("###### elapsed time:{} sec", stopWatch.getTotalTimeSeconds());
		}
	}

	@Test
	@Transactional
	@Rollback(false)
	public void speedtest() {
		StopWatch stopWatch = new StopWatch();
		try {
			int n = JdbcTestUtils.deleteFromTables(jdbcTemplate, "aaaaaa");
			LOG.info("deleted rows :{}", n);
			final int X = 10000;
			final int Y = 10;
			stopWatch.start();
			for (int i = 0; i < Y; i++) {
				final List<Object[]> list = new ArrayList<Object[]>();
				for (int j = 0; j < X; j++) {
					Object[] args = new Object[] { i * X + j, "もくめ", new Date(), new Date() };
					list.add(args);
				}
				BEGIN();
				INSERT_INTO("aaaaaa");
				VALUES("column1", "?");
				VALUES("column2", "?");
				VALUES("cretime", "?");
				VALUES("uptime", "?");
				jdbcTemplate.batchUpdate(SQL(), list);
				LOG.info("{}回目", i + 1);
			}
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			stopWatch.stop();
			LOG.info("###### elapsed time:{} sec", stopWatch.getTotalTimeSeconds());
		}
	}

	/**
	 * XA transactionのテスト
	 *
	 * @throws Exception
	 */
	@Test
	public void xaTxTest() throws Exception {
		txPerform.execute();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public static class XaTxPerformer {
		@Value("#{jdbcTemplate1}")
		private JdbcTemplate jdbcTemplate1;

		@Value("#{jdbcTemplate2}")
		private JdbcTemplate jdbcTemplate2;

		public void execute() {
			try {
				int i = JdbcTestUtils.deleteFromTables(jdbcTemplate1, "aaaaaa");
				// i = JdbcTestUtils.deleteFromTables(jdbcTemplate2, "bbbbbb");

				BEGIN();
				INSERT_INTO("aaaaaa");
				VALUES("column1", "?");
				VALUES("column2", "?");
				VALUES("cretime", "?");
				VALUES("uptime", "?");
				final String query1 = SQL();
				jdbcTemplate1.update(query1, 10, "もくめ", new Date(), new Date());

				BEGIN();
				INSERT_INTO("bbbbbb");
				VALUES("column1", "?");
				VALUES("column2", "?");
				VALUES("cretime", "?");
				VALUES("uptime", "?");
				final String query2 = SQL();
				jdbcTemplate2.update(query2, 20, "もくめ", new Date(), new Date());
			} catch (DataAccessException e) {
				LOG.info("##### EX :{}", e.toString());
				throw e;
			}
		}
	}

}
