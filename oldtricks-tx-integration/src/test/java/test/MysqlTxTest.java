package test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.TimeUnit;

import oldtricks.ex.util.DateTimeUtil;

import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MysqlTxTest {
	@Autowired(required = true)
	private DBTest target;
	@Autowired(required = true)
	private Executor executor;

	@Test
	public void AutoCommit確認() throws Exception {
		target.scenario1();
	}

	/**
	 * 2つのスレッドで同一のPKをselect for update した場合は、片方がブロックされる。
	 *
	 * @throws Exception
	 */
	@Test
	public void SelectForUpdate確認() throws Exception {
		final int taskCnt = 2;
		ExecutorCompletionService<?> completionService = new ExecutorCompletionService<Object>(executor);
		for (int i = 0; i < taskCnt; i++) {
			final int id = i;
			completionService.submit(new Runnable() {
				public void run() {
					try {
						target.scenario2(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, null);
		}
		for (int i = 0; i < taskCnt; i++) {
			completionService.take();
		}
	}

	/**
	 * 2つのスレッドで異なるのPKをselect for update した場合は、ブロックされない。
	 *
	 * @throws Exception
	 */
	@Test
	public void SelectForUpdate確認2() throws Exception {
		final int taskCnt = 2;
		ExecutorCompletionService<?> completionService = new ExecutorCompletionService<Object>(executor);
		for (int i = 0; i < taskCnt; i++) {
			final int id = i;
			completionService.submit(new Runnable() {
				public void run() {
					try {
						target.scenario2(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, null);
		}
		for (int i = 0; i < taskCnt; i++) {
			completionService.take();
		}
	}

	public static boolean isAutoCommit(JdbcTemplate jdbcTemplate) {
		final String query = new SQL() {
			{
				SELECT("@@autocommit");

			}
		}.toString();
		return jdbcTemplate.queryForObject(query,Integer.class) == 1;
	}

	public static List<Map<String, Object>> selectForUpdate(JdbcTemplate jdbcTemplate, int id) {
		final String sql = new SQL() {
			{
				SELECT("*");
				FROM("sample");
				WHERE("id = ?");
			}
		}.toString() + " FOR UPDATE";
		Object[] args = new Object[] { id };
		return jdbcTemplate.queryForList(sql, args);
	}

	public static int insert(JdbcTemplate jdbcTemplate, int id, String name) {
		final String sql = new SQL() {
			{
				INSERT_INTO("sample");
				VALUES("id", "?");
				VALUES("name", "?");
			}
		}.toString();
		Object[] args = new Object[] { id, name };
		return jdbcTemplate.update(sql, args);
	}

	public static int update(JdbcTemplate jdbcTemplate, int id, String name) {
		final String sql = new SQL() {
			{
				UPDATE("sample");
				SET("name = ?");
				WHERE("id = ?");
			}
		}.toString();
		Object[] args = new Object[] { name, id };
		return jdbcTemplate.update(sql, args);
	}

	/**
	 *
	 * @author kubota
	 *
	 */
	public static class DBTest {
		@Autowired
		@Qualifier("spring.JdbcTemplate")
		private JdbcTemplate jdbcTemplate;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		@Transactional
		public void scenario1() throws Exception {
			boolean isAutoCommit = isAutoCommit(jdbcTemplate);
			selectForUpdate(jdbcTemplate, 1);
			System.out.println("AutoCommit=" + isAutoCommit);
			assertFalse(isAutoCommit);
		}

		/**
		 * 行ロックしてからUPDATE
		 *
		 * @param id
		 *
		 * @throws Exception
		 */
		@Transactional
		public void scenario2(int id) throws Exception {
			selectForUpdate(jdbcTemplate, id);
			update(jdbcTemplate, id, DateTimeUtil.now().toString());
			TimeUnit.SECONDS.sleep(10);
		}
	}
}
