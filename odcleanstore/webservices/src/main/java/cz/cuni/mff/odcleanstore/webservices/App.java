/**
 * 
 */
package cz.cuni.mff.odcleanstore.webservices;

import javax.xml.ws.Endpoint;

import org.restlet.Component;
import org.restlet.data.Protocol;

import cz.cuni.mff.odcleanstore.webservices.scraper.Scraper;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class App {
	public static void main(String[] args) {

		try {
			

			Component component = new Component();
			component.getServers().add(Protocol.HTTP, 8087);
			component.getDefaultHost().attach(new cz.cuni.mff.odcleanstore.webservices.user.Root());
			component.start();
			
			Endpoint.publish("http://localhost:8088/odcleanstore/scraper", new Scraper());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
