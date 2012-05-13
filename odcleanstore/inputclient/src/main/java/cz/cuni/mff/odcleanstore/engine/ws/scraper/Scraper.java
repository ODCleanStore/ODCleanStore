package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "Scraper", targetNamespace = "http://scraper.ws.engine.odcleanstore.mff.cuni.cz/")
@XmlSeeAlso({ ObjectFactory.class })
public interface Scraper {

	@WebMethod
	@RequestWrapper(localName = "insert", targetNamespace = "http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", className = "cz.cuni.mff.odcleanstore.engine.ws.scraper.Insert")
	@ResponseWrapper(localName = "insertResponse", targetNamespace = "http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", className = "cz.cuni.mff.odcleanstore.engine.ws.scraper.InsertResponse")
	public void insert(@WebParam(name = "user", targetNamespace = "") String user, @WebParam(name = "password", targetNamespace = "") String password,
			@WebParam(name = "metadata", targetNamespace = "") Metadata metadata, @WebParam(name = "rdfXmlPayload", targetNamespace = "") String rdfXmlPayload) throws InsertException_Exception;
}
