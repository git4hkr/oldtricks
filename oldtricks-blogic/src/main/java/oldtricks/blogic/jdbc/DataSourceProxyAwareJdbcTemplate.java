package oldtricks.blogic.jdbc;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import oldtricks.blogic.datasource.DataSourceProxy;

/**
 * データソースルーティング
 *
 * @author devuser
 *
 */
public class DataSourceProxyAwareJdbcTemplate extends JdbcTemplate {

	@Override
	public DataSource getDataSource() {
		DataSource ds = super.getDataSource();
		if (ds instanceof DataSourceProxy) {
			DataSource _ds = ((DataSourceProxy) ds).getRowDataSource();
			if (_ds != null)
				ds = _ds;
		}
		return ds;
	}

}
