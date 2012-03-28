/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.core;

import java.security.CodeSource;
import java.security.SecureClassLoader;

import cz.cuni.mff.odcleanstore.common.sysutil.JarReader;

/**
 * Odcleanstore custom module loader and runner.
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class CustomModuleRunner {
	private String _jarName;
	private static Thread _thread;
	private boolean _threadIsRunned;

	public CustomModuleRunner(String jarName) {
		_jarName = jarName;
	}

	public void runOnce() {

		// TODO Add full multithreading support

		if (!_threadIsRunned) {
			_threadIsRunned = true;
			_thread = new Thread(new ThreadRunner());
			_thread.start();
		}
	}

	public void join() throws InterruptedException {
		if (_thread != null) {
			_thread.join();
		}
	}

	@Deprecated
	public void destroy() {

		// TODO Implement less destructive method?

		_thread.destroy();
	}

	public static boolean isInCustomModuleThread() {
		return Thread.currentThread().getContextClassLoader() instanceof CustomModuleClassLoader;
	}

	private class ThreadRunner implements Runnable {

		private CustomModuleClassLoader _classLoader;

		public void run() {
			try {
				_classLoader = new CustomModuleClassLoader();

				// From this point, thread code is secure, see also EngineSecurityManager
				_thread.setContextClassLoader(_classLoader);

				String mainClassName = _classLoader.getMainClassName();
				Class<?> cls = _classLoader.loadClass(mainClassName);
				java.lang.reflect.Method main = cls.getMethod("main", new Class[] { String[].class });

				main.invoke(null, new Object[] { new String[] {} });
			} catch (Exception e) {

				// TODO Change security for logging and logging

				e.printStackTrace();
			} finally {
				_classLoader = null;
				_thread = null;
				_jarName = null;
			}
		}
	}

	private class CustomModuleClassLoader extends SecureClassLoader {
		private JarReader _jarReader;

		public CustomModuleClassLoader() {
			super(CustomModuleRunner.this.getClass().getClassLoader());
			_jarReader = new JarReader(_jarName);
		}

		public String getMainClassName() {
			return _jarReader.getMainClassName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.ClassLoader#findClass(java.lang.String)
		 */
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			byte buf[];
			Class<?> cl;

			try {
				buf = _jarReader.getClassBytes(name);
				cl = defineClass(name, buf, 0, buf.length, (CodeSource) null);
				return cl;
			} catch (Exception e) {
				return null;
			}
		}
	}
}
