/**
 * 
 */
package cz.cuni.mff.odcleanstore.webservices;

import javax.xml.ws.Endpoint;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class App {
	public static void main(String[] args) {
		 Endpoint.publish("http://localhost:8088/odcleanstore/scrapper", new Scraper());
	}
}
