package cz.cuni.mff.odcleanstore.engine.inputws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Internal odcs-inputclient jax-ws implementation class.
 * Direct usage not recommended, may be removed in next versions.
 *  @author Petr Jerman
 */
@WebService(name = "InputWS", targetNamespace = "http://inputws.engine.odcleanstore.mff.cuni.cz/")
@XmlSeeAlso({ ObjectFactory.class })
public interface InputWS {

	@WebMethod
	@RequestWrapper(localName = "insert", targetNamespace = "http://inputws.engine.odcleanstore.mff.cuni.cz/", className = "cz.cuni.mff.odcleanstore.engine.inputws.Insert")
	@ResponseWrapper(localName = "insertResponse", targetNamespace = "http://inputws.engine.odcleanstore.mff.cuni.cz/", className = "cz.cuni.mff.odcleanstore.engine.inputws.InsertResponse")
	public void insert(@WebParam(name = "user", targetNamespace = "") String user, @WebParam(name = "password", targetNamespace = "") String password,
			@WebParam(name = "metadata", targetNamespace = "") Metadata metadata, @WebParam(name = "payload", targetNamespace = "") String payload) throws InsertException_Exception;
}
