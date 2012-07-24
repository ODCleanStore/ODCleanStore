package cz.cuni.mff.odcleanstore.engine.inputws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Internal odcs-inputclient jax-ws implementation class.
 * Direct usage not recommended, may be removed in next versions.
 *  @author Petr Jerman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "insert", propOrder = { "user", "password", "metadata", "payload" })
public class Insert {

	protected String user;
	protected String password;
	protected Metadata metadata;
	protected String payload;

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

	public String getPayload() {
		return payload;
	}

	public void setPayload(String value) {
		this.payload = value;
	}
}
