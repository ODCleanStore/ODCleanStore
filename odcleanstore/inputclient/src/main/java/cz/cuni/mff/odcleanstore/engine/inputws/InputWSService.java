package cz.cuni.mff.odcleanstore.engine.inputws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * Internal odcs-inputclient jax-ws implementation class.
 * Direct usage not recommended, may be removed in next versions.
 *  @author Petr Jerman
 */
public class InputWSService extends Service {
	
	public static InputWSService create(String serviceLocation) throws MalformedURLException {
			URL url = null;
            URL baseUrl;
            baseUrl = cz.cuni.mff.odcleanstore.engine.inputws.InputWSService.class.getResource(".");
            url = new URL(baseUrl, serviceLocation + "?wsdl");
            
            return new InputWSService(url);
	}
	
	private InputWSService(URL wsdlLocation) {
		super(wsdlLocation, new QName("http://inputws.engine.odcleanstore.mff.cuni.cz/", "InputWSService"));
	}
	
	public InputWS getInputWSPort() {
		return super.getPort(new QName("http://inputws.engine.odcleanstore.mff.cuni.cz/", "InputWSPort"), InputWS.class);
	}
}
