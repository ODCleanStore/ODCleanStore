package cz.cuni.mff.odcleanstore.engine.inputws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Internal odcs-inputclient jax-ws implementation class.
 * Direct usage not recommended, may be removed in next versions.
 *  @author Petr Jerman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metadata", propOrder = { "uuid", "publishedBy", "source", "license", "dataBaseUrl", "provenance", "pipelineName" })
public class Metadata {

	protected String uuid;
	@XmlElement(nillable = true)
	protected List<String> publishedBy;
	@XmlElement(nillable = true)
	protected List<String> source;
	@XmlElement(nillable = true)
	protected List<String> license;
	protected String dataBaseUrl;
	protected String provenance;
	protected String pipelineName;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String value) {
		this.uuid = value;
	}

	public List<String> getPublishedBy() {
		if (publishedBy == null) {
			publishedBy = new ArrayList<String>();
		}
		return this.publishedBy;
	}

	public List<String> getSource() {
		if (source == null) {
			source = new ArrayList<String>();
		}
		return this.source;
	}

	public List<String> getLicense() {
		if (license == null) {
			license = new ArrayList<String>();
		}
		return this.license;
	}

	public String getDataBaseUrl() {
		return dataBaseUrl;
	}

	public void setDataBaseUrl(String value) {
		this.dataBaseUrl = value;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String value) {
		this.provenance = value;
	}
	
	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String value) {
		this.pipelineName = value;
	}
}
