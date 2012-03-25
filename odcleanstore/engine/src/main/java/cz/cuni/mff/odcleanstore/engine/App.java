/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

public class App {

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
	}
}
