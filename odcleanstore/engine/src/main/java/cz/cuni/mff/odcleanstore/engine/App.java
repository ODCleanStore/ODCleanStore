/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import cz.cuni.mff.odcleanstore.engine.core.EngineSecurityManager;

// FIXME Exception handling is intentionally omitted in first phase of prototype in all engine module !!! 

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class App {
	private static App _app = null;

	public static App currentApp() {
		return _app;
	}

	public static void main(String[] args) {
		try {
			_app = new App();
			_app.main();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private App() {
	}

	private void main() throws InterruptedException {

		// TODO Add init basic logging mechanism

		if (!checkJavaVersion())
			return;

		// TODO Add reading config file

		if (!checkAndSetEngineSecurity())
			return;

		// TODO Detect runtime environment - NT Service, Daemon, Java application or JavaAppServer and run
		// appropriate code with full logging mechanism initialized

		System.out.println("Odcleanstorage started and properly terminated");
	}

	private boolean checkJavaVersion() {

		// TODO Find better check Java version. If exist and if running as NT Service, then must check
		// version in registry :( !!!

		String version = System.getProperty("java.version");
		int pos = 0, count = 0;
		for (; pos < version.length() && count < 2; pos++) {
			if (version.charAt(pos) == '.')
				count++;
		}

		return Double.parseDouble(version.substring(0, pos - 1)) >= 1.6;
	}

	private boolean checkAndSetEngineSecurity() {

		// TODO Add choice by config file

		new EngineSecurityManager();
		return true;
	}
}
