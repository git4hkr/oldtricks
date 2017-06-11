package oldtricks.blogic.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class CustomSqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {
	@Override
	public SqlSessionFactory build(Configuration config) {
		return new DataSourceProxyAwareSqlSessionFactory(config);
	}

}
