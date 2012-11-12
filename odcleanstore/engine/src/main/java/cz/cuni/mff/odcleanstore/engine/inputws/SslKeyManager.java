package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

public class SslKeyManager {

	private static final String JKS_CERTIFICATE_NAME = "odcs-engine.keystore";
	private static final String STORE_PASSWORD = "a.&yu2-qS23#4";
	private static final String KEY_PASSWORD = "bng,<j8*-+aS4n";

	public static KeyManager[] getKeys() {
		try {
			KeyStore ks = KeyStore.getInstance("JKS");

			String ksName = JKS_CERTIFICATE_NAME;
			char ksPass[] = STORE_PASSWORD.toCharArray();
			char ctPass[] = KEY_PASSWORD.toCharArray();
			try {
				ks.load(new FileInputStream(ksName), ksPass);
			}
			catch (Exception e) {
				return null;		
			}
			ks.load(new FileInputStream(ksName), ksPass);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ctPass);
			return kmf.getKeyManagers();

		} catch (Exception e) {
			return null;
		}
	}
}
