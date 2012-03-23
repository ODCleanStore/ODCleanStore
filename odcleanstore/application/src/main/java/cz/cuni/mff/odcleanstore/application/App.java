/**
 * 
 */
package cz.cuni.mff.odcleanstore.application;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Enumeration;

import cz.cuni.mff.odcleanstore.application.core.JarReader;

//import java.io.*;
//import java.lang.reflect.*;
//import java.net.*;
//import java.security.*;
//import java.util.*;
//import java.util.jar.*;

//FIXME Doimplementovat mechanismus vyjimek

public class App {

	// private static final String jarName = "Component";
	// private static final String className = "ComponentMain";

	private static App app = null;

	public static App currentApp() {
		return app;
	}

	public static void main(String[] args) {
		try {
			app = new App();
			app.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private App() {
	}

	private void run() {

		JarReader jr = new JarReader("C:/Dev/q.jar");

		for (String name : jr.getEntryNames()) {
			System.out.println(name);
		}

		// TODO postupne rozchozovane casti

		// security
		// threading exception mechanismus
		// logovani
		// ... etc

		// bokem - small logovani na zadost

		// setSecurityManager();

		// Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
		// public void uncaughtException(Thread t, Throwable e) {
		// App.this.uncaughtException(t, e);
		// }
		// });

		// Class<App> self = App.class;
		// System.out.println(self.getProtectionDomain().getCodeSource().getLocation().toString());
	}

	// private void uncaughtException(Thread thread, Throwable e) {
	// e.printStackTrace();
	// }
	//
	// private void setSecurityManager() {
	// SecurityManager sm = System.getSecurityManager();
	// if (sm != null) {
	// // TODO Otestovat je-li sm standardni SecurityManager a zkusit nahradit nasim
	// // SecurityManagerImpl, nejde-li - fatal konec (nebo mod nosecurity?)
	// //
	// // OPTIONAL Jde-li ziskat standardni permissions a kombinovat je s nasimi,
	// // v ClassLoaderu sloucit standardni permissions s nasimi (and) a neudelat fatal konec
	// // OTESTOVAT, ZKUSIT V PRIPADE MULTITASKINGU !!!
	// return;
	// } else {
	// System.setSecurityManager(new SecurityManagerImpl());
	// }
	// }
}
