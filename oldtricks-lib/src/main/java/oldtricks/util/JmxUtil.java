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
package oldtricks.util;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

public abstract class JmxUtil {
	public static final String GC_NAME_CONCURRENT_MARKSWEEP = "ConcurrentMarkSweep";
	public static final String GC_NAME_DEFAULT_MARKSWEEP = "PS MarkSweep";

	public static JMXConnector getJMXConnector(String host, int port) throws IOException {
		return getJMXConnector(host, port, null, null);
	}

	public static JMXConnector getJMXConnector(String host, int port, String usr, String pwd) throws IOException {
		String serviceUrl = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
		if (usr == null || usr.trim().length() <= 0 || pwd == null || pwd.trim().length() <= 0) {
			return JMXConnectorFactory.connect(new JMXServiceURL(serviceUrl));
		}
		Map<String, Object> envMap = new HashMap<String, Object>();
		envMap.put("jmx.remote.credentials", new String[] { usr, pwd });
		envMap.put(Context.SECURITY_PRINCIPAL, usr);
		envMap.put(Context.SECURITY_CREDENTIALS, pwd);
		return JMXConnectorFactory.connect(new JMXServiceURL(serviceUrl), envMap);
	}

	public static RuntimeMXBean getRuntimeMXBeanFromRemote(MBeanServerConnection mBeanServerConn) throws IOException {
		return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConn, ManagementFactory.RUNTIME_MXBEAN_NAME,
				RuntimeMXBean.class);
	}

	public static MemoryMXBean getMemoryMXBeanFromRemote(MBeanServerConnection mBeanServerConn) throws IOException {
		return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConn, ManagementFactory.MEMORY_MXBEAN_NAME,
				MemoryMXBean.class);
	}

	public static OperatingSystemMXBean getOperatingSystemMXBeanFromRemote(MBeanServerConnection mBeanServerConn)
			throws IOException {
		return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConn,
				ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
	}

	public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeansFromRemote(
			MBeanServerConnection mBeanServerConn) throws MalformedObjectNameException, NullPointerException,
			IOException {
		List<GarbageCollectorMXBean> gcMXBeans = new ArrayList<GarbageCollectorMXBean>();
		ObjectName gcAllObjectName = new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
		Set<ObjectName> gcMXBeanObjectNames = mBeanServerConn.queryNames(gcAllObjectName, null);
		for (ObjectName on : gcMXBeanObjectNames) {
			GarbageCollectorMXBean gc = ManagementFactory.newPlatformMXBeanProxy(mBeanServerConn,
					on.getCanonicalName(), GarbageCollectorMXBean.class);
			gcMXBeans.add(gc);
		}
		return gcMXBeans;
	}

	public static GarbageCollectorMXBean getGarbageCollectorMXBeansFromRemote(MBeanServerConnection mBeanServerConn,
			String name) throws MalformedObjectNameException, NullPointerException, IOException {
		ObjectName gcAllObjectName = new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
		Set<ObjectName> gcMXBeanObjectNames = mBeanServerConn.queryNames(gcAllObjectName, null);
		for (ObjectName on : gcMXBeanObjectNames) {
			GarbageCollectorMXBean gc = ManagementFactory.newPlatformMXBeanProxy(mBeanServerConn,
					on.getCanonicalName(), GarbageCollectorMXBean.class);
			if (gc.getName().equals(name))
				return gc;
		}
		return null;
	}

	/**
	 * プラットフォーム{@link MBeanServer}に MBeanを登録します。
	 *
	 * @param object
	 *            MBeanオブジェクト
	 * @param name
	 *            オブジェクト名（ドメイン:キー=値,キー=値）形式。
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 */
	public static synchronized void registerMBean(Object object, String name) throws InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException,
			InstanceNotFoundException {
		ObjectName objectName = new ObjectName(name);
		try {
			// 未登録の場合はInstanceNotFoundExceptionがスローされる。
			ManagementFactory.getPlatformMBeanServer().getObjectInstance(objectName);
		} catch (InstanceNotFoundException register) {
			ManagementFactory.getPlatformMBeanServer().registerMBean(object, objectName);
		}
	}

	/**
	 * プラットフォーム{@link MBeanServer}から MBeanを削除します。
	 *
	 * @param name
	 *            オブジェクト名（ドメイン:キー=値,キー=値）形式。
	 * @throws MBeanRegistrationException
	 * @throws InstanceNotFoundException
	 * @throws MalformedObjectNameException
	 */
	public static void unregisterMBean(String name) throws MBeanRegistrationException, InstanceNotFoundException,
			MalformedObjectNameException {
		ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(name));
	}
}
