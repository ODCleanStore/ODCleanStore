package cz.cuni.mff.odcleanstore.wsclient;

import java.util.ArrayList;
import java.util.List;

public final class Metadata {

	private String _uuid;
	private List<String> _publishedBy;
	private List<String> _source;
	private List<String> _license;
	private String _rdfXmlProvenance;

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String value) {
		this._uuid = value;
	}

	public List<String> getPublishedBy() {
		if (_publishedBy == null) {
			_publishedBy = new ArrayList<String>();
		}
		return this._publishedBy;
	}

	public List<String> getSource() {
		if (_source == null) {
			_source = new ArrayList<String>();
		}
		return this._source;
	}

	public List<String> getLicense() {
		if (_license == null) {
			_license = new ArrayList<String>();
		}
		return this._license;
	}

	public String getRdfXmlProvenance() {
		return _rdfXmlProvenance;
	}

	public void setRdfXmlProvenance(String value) {
		this._rdfXmlProvenance = value;
	}
}
