package oldtricks.blogic.statistics;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import oldtricks.blogic.Constant;
import oldtricks.util.JmxUtil;
import oldtricks.util.StringUtil;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;

public class StatisticsSupport {
	protected static final int DEFAULT_WINDOW_SIZE = 100000;// 1エントリーあたり1Mbyte程度のHeapを消費する
	protected ConcurrentMap<String, Statistics> stats = new ConcurrentHashMap<>();

	public void addValue(String key, double value, int windowSize) throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException,
			InstanceNotFoundException {
		if (StringUtil.isNotBlank(key)) {
			stats.putIfAbsent(key, new Statistics(key, windowSize));
			stats.get(key).addValue(value);
		}
	}

	public void clear() {
		for (Entry<String, Statistics> entry : stats.entrySet()) {
			entry.getValue().unregisterMBean();
		}
		stats = new ConcurrentHashMap<>();
	}

	public String dumpAllStat() {
		StringBuilder builder = new StringBuilder();
		final String format = "%12s %12s %15s %15s %12s %15s %15s %15s %12s   %s";
		String[] header = new String[] { "Min", "Max", "90%ile", "95%ile", "SampleNum", "TotalMin", "TotalMax",
				"TotalAvg", "TotalNum", "Name" };
		String line = String.format(format, (Object[]) header);
		builder.append(line + SystemUtils.LINE_SEPARATOR);
		for (Entry<String, Statistics> entry : stats.entrySet()) {
			line = String.format(format, entry.getValue().getMin(), entry.getValue().getMax(), entry.getValue()
					.get90Percentile(), entry.getValue().get95Percentile(), entry.getValue().getSampleNum(), entry
					.getValue().getTotalMin(), entry.getValue().getTotalMax(), entry.getValue().getTotalAvg(), entry
					.getValue().getTotalSample(), entry.getKey());
			builder.append(line + SystemUtils.LINE_SEPARATOR);
		}
		return builder.toString();
	}

	private static class Statistics implements StatisticsMBean {
		private static final String JMX_DOMAIN = Constant.PRODUCT_PACKAGE;
		private ObjectName objectName;
		private DescriptiveStatistics stat;
		private AtomicLong total = new AtomicLong();
		private double totalMin = Double.MAX_VALUE;
		private double totalMax = Double.MIN_VALUE;
		private double totalSum = 0;

		public Statistics(String key, int windowSize) throws MalformedObjectNameException,
				InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException,
				InstanceNotFoundException {
			super();
			Hashtable<String, String> table = new Hashtable<>();
			table.put("Name", "BLogicStatistics");
			table.put("Type", key);
			objectName = new ObjectName(JMX_DOMAIN, table);
			stat = new SynchronizedDescriptiveStatistics(windowSize);
			JmxUtil.registerMBean(this, objectName.toString());
		}

		public void unregisterMBean() {
			try {
				JmxUtil.unregisterMBean(objectName.toString());
			} catch (MBeanRegistrationException | InstanceNotFoundException | MalformedObjectNameException ignore) {
			}
		}

		@SuppressWarnings("unused")
		public ObjectName getObjectName() {
			return objectName;
		}

		public void addValue(double v) {
			stat.addValue(v);
			total.incrementAndGet();
			synchronized (this) {
				totalSum = totalSum + v;
				if (totalMin > v)
					totalMin = v;
				if (totalMax < v)
					totalMax = v;
			}
		}

		@Override
		public long getTotalSample() {
			return total.get();
		}

		@Override
		public long getTotalAvg() {
			return (long) (totalSum / total.get());
		}

		@Override
		public long getTotalMin() {
			return (long) totalMin;
		}

		@Override
		public long getTotalMax() {
			return (long) totalMax;
		}

		@Override
		synchronized public void clear() {
			total.set(0);
			totalMax = Double.MIN_VALUE;
			totalMin = Double.MAX_VALUE;
			totalSum = 0;
			stat.clear();
		}

		@Override
		public long getMax() {
			return (long) stat.getMax();
		}

		@Override
		public long getMin() {
			return (long) stat.getMin();
		}

		@Override
		public long getSampleNum() {
			return stat.getN();
		}

		@Override
		public long get90Percentile() {
			return (long) stat.getPercentile(90);
		}

		@Override
		public long get95Percentile() {
			return (long) stat.getPercentile(95);
		}

		@Override
		public int getWindowSize() {
			return stat.getWindowSize();
		}
	}

	public interface StatisticsMBean {

		public void clear();

		public long getTotalSample();

		public long getTotalAvg();

		public long getTotalMin();

		public long getTotalMax();

		public long getMax();

		public long getMin();

		public long getSampleNum();

		public long get90Percentile();

		public long get95Percentile();

		public int getWindowSize();
	}

}
