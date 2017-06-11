package oldtricks.blogic.datasource;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;

import oldtricks.blogic.BLogicAvailabilityZoneResolver;
import oldtricks.blogic.BLogicDataSourceConfig;
import oldtricks.blogic.BLogicShardNoResolver;
import oldtricks.blogic.springcontext.BLogicFilterWithLifeCycle;

public class BLogicDataSourceRouterFilter implements BLogicFilterWithLifeCycle {

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
		try {
			BLogicDataSourceRouter.setUniqueRsourceId(key);
			return next.invoke(target, method, args);
		} finally {
			BLogicDataSourceRouter.clearUniqueResourceId();
		}
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
	}

	@Override
	public void destory() throws Throwable {
	}

}
