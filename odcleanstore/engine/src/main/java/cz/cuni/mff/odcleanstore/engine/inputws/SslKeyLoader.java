package cz.cuni.mff.odcleanstore.engine.inputws;

import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

/**
 * Class for loading internal certificate for ssl server.
 * 
 *  @author Petr Jerman
 */
public class SslKeyLoader {

	private static final String JKS_CERTIFICATE_RESOURCE_NAME = "/odcs-engine.keystore";
	private static final String STORE_PASSWORD = "a.&yu2-qS23#4";
	private static final String KEY_PASSWORD = "bng,<j8*-+aS4n";

	/**
	 * @return KeyManager array for ssl server
	 */
	public static KeyManager[] getKeys() {
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			char ksPass[] = STORE_PASSWORD.toCharArray();
			char ctPass[] = KEY_PASSWORD.toCharArray();
			try {
				ks.load(SslKeyLoader.class.getResourceAsStream(JKS_CERTIFICATE_RESOURCE_NAME), ksPass);
			}
			catch (Exception e) {
				return null;		
			}
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ctPass);
			return kmf.getKeyManagers();

		} catch (Exception e) {
			return null;
		}
	}
}
