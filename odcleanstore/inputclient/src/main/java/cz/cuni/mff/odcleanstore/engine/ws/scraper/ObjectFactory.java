package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * Internal odcs-inputclient jax-ws implementation class.
 * Direct usage not recommended, may be removed in next versions.
 *  @author Petr Jerman
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _InsertException_QNAME = new QName("http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", "InsertException");
	private final static QName _Insert_QNAME = new QName("http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", "insert");
	private final static QName _InsertResponse_QNAME = new QName("http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", "insertResponse");

	public ObjectFactory() {
	}

	public Metadata createMetadata() {
		return new Metadata();
	}

	public InsertResponse createInsertResponse() {
		return new InsertResponse();
	}

	public InsertException createInsertException() {
		return new InsertException();
	}

	public Insert createInsert() {
		return new Insert();
	}

	@XmlElementDecl(namespace = "http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", name = "InsertException")
	public JAXBElement<InsertException> createInsertException(InsertException value) {
		return new JAXBElement<InsertException>(_InsertException_QNAME, InsertException.class, null, value);
	}

	@XmlElementDecl(namespace = "http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", name = "insert")
	public JAXBElement<Insert> createInsert(Insert value) {
		return new JAXBElement<Insert>(_Insert_QNAME, Insert.class, null, value);
	}

	@XmlElementDecl(namespace = "http://scraper.ws.engine.odcleanstore.mff.cuni.cz/", name = "insertResponse")
	public JAXBElement<InsertResponse> createInsertResponse(InsertResponse value) {
		return new JAXBElement<InsertResponse>(_InsertResponse_QNAME, InsertResponse.class, null, value);
	}
}
