package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class ScraperService extends Service {
	
	public static ScraperService create(String serviceLocation) throws MalformedURLException {
			URL url = null;
            URL baseUrl;
            baseUrl = cz.cuni.mff.odcleanstore.engine.ws.scraper.ScraperService.class.getResource(".");
            url = new URL(baseUrl, serviceLocation + "?wsdl");
            
            return new ScraperService(url);
	}
	
	private ScraperService(URL wsdlLocation) {
		super(wsdlLocation, new QName("http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", "ScraperService"));
	}
	
	public Scraper getScraperPort() {
		return super.getPort(new QName("http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", "ScraperPort"), Scraper.class);
	}
}
