package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Internal odcs-inputclient jax-ws implementation class.
 * Direct usage not recommended, may be removed in next versions.
 *  @author Petr Jerman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertException", propOrder = { "id", "message", "moreInfo" })
public class InsertException {

	protected int id;
	protected String message;
	protected String moreInfo;

	public int getId() {
		return id;
	}

	public void setId(int value) {
		this.id = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String value) {
		this.message = value;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String value) {
		this.moreInfo = value;
	}
}
