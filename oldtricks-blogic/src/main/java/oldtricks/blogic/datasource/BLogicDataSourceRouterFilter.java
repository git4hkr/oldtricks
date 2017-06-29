package oldtricks.blogic.datasource;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;
import oldtricks.blogic.BLogicAvailabilityZoneResolver;
import oldtricks.blogic.BLogicDataSourceConfig;
import oldtricks.blogic.BLogicShardNoResolver;
import oldtricks.blogic.springcontext.BLogicFilterWithLifeCycle;

@Slf4j
public class BLogicDataSourceRouterFilter implements BLogicFilterWithLifeCycle {
	private BLogicDataSourceRegistry bLogicDataSourceRegistry;

	@Override
	public boolean accept(Method method) {
		BLogicDataSourceConfig anno = method.getAnnotation(BLogicDataSourceConfig.class);
		return anno != null;
	}

	@Override
	public Object intercept(Object target, Method method, Object[] args, Next next) throws Throwable {
		BLogicDataSourceConfig config = method.getAnnotation(BLogicDataSourceConfig.class);
		BLogicDataSourceKey key = new BLogicDataSourceKey(config.type(), config.readReplica(),
				getShardNo(config.type(), target, args), getAvailabilityZone(target));
		List<String> urls = bLogicDataSourceRegistry.getUrls(key);
		Assert.notNull(urls,
				"Data source for the specified key can not be found in datasource registry. key=" + key.toString());
		Throwable ex = null;
		for (String url : urls) {
			try {
				BLogicDataSourceRouter.setUniqueRsourceId(url);
				return next.invoke(target, method, args);
			} catch (Throwable e) {
				ex = e;
				log.info("datasource access is failed. url=[{}]. trying to recover with next datasource.", url);
			} finally {
				BLogicDataSourceRouter.clearUniqueResourceId();
			}
		}
		if (ex != null) {
			throw ex;
		}
		// 設計上到達不能
		return null;
	}

	int getShardNo(String type, Object target, Object[] args) throws Exception {
		if (BLogicDataSourceConfig.TYPE_MASTER.equals(type))
			return 0;
		if (target instanceof BLogicShardNoResolver)
			return ((BLogicShardNoResolver) target).resolveShardNo();
		return 0;
	}

	String getAvailabilityZone(Object target) throws Exception {
		if (target instanceof BLogicAvailabilityZoneResolver)
			return ((BLogicAvailabilityZoneResolver) target).resolveAZ();
		return "default";
	}

	@Override
	public void init(Object target, ApplicationContext applicationContext) throws Throwable {
		this.bLogicDataSourceRegistry = applicationContext.getBean(BLogicDataSourceRegistry.class);
	}

	@Override
	public void destory() throws Throwable {
	}

}
