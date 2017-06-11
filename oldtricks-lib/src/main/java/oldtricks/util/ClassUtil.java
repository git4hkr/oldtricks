package oldtricks.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.lang3.ClassUtils;

public abstract class ClassUtil extends ClassUtils {
	public static void addClassPath(ClassLoader classLoader, String path) throws ReflectiveOperationException,
			MalformedURLException {
		if (classLoader instanceof URLClassLoader) {
			// URLClassLoaderであることが前提
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			// ロードするURLを追加する
			method.invoke(classLoader, new File(path).toURI().toURL());
		}
	}
}
