package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "insert", propOrder = { "user", "password", "metadata", "rdfXmlPayload" })
public class Insert {

	protected String user;
	protected String password;
	protected Metadata metadata;
	protected String rdfXmlPayload;

	public String getUser() {
		return user;
	}

	public void setUser(String value) {
		this.user = value;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String value) {
		this.password = value;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata value) {
		this.metadata = value;
	}

	public String getRdfXmlPayload() {
		return rdfXmlPayload;
	}

	public void setRdfXmlPayload(String value) {
		this.rdfXmlPayload = value;
	}
}
