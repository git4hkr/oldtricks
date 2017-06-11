package oldtricks.launch;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebAppLauncher {

	public static final String TEMP_DIR_PROP_KEY = "launcher.jetty.tmpdir";

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("");
			System.out.println("sorry, we need args.");
			System.out.println("");
			return;
		}

		try {
			WebAppContext webapp = new WebAppContext(args[0], "/");
			webapp.setParentLoaderPriority(true);
			String tempDir = System.getProperty(TEMP_DIR_PROP_KEY);
			if (tempDir != null)
				webapp.setTempDirectory(new File(tempDir));
			webapp.setServer(new Server());
			webapp.preConfigure();
			webapp.configure();
			Thread.currentThread().setContextClassLoader(webapp.getClassLoader());
			String[] blArgs = {};
			if (args.length > 1)
				blArgs = Arrays.copyOfRange(args, 1, args.length);
			invokeLauncher(blArgs, webapp.getClassLoader());
			// SpringLauncherがexitするため、後処理はShutdownHookで行う。
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void invokeLauncher(String[] args, ClassLoader classLoader) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = getMainMethod(classLoader);
		method.invoke(null, new Object[] { args });
	}

	static Class<?> getMainClass(ClassLoader classLoader) throws ClassNotFoundException {
		return classLoader.loadClass("oldtricks.blogic.tool.SpringLauncher");
	}

	static Method getMainMethod(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
		Method m = getMainClass(classLoader).getMethod("main", new Class[] { String[].class });

		int modifiers = m.getModifiers();
		if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
			if (m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE) {
				return m;
			}
		}
		throw new NoSuchMethodException("public static void main(String[] args) in " + getMainClass(classLoader));
	}
}
