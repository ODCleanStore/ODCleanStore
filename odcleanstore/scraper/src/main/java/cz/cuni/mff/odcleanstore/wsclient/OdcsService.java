package cz.cuni.mff.odcleanstore.wsclient;

import java.net.MalformedURLException;

import cz.cuni.mff.odcleanstore.engine.ws.scraper.InsertException_Exception;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.Scraper;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ScraperService;

public final class OdcsService {

	private ScraperService _scraperService;
	private Scraper _scraperPort;

	public OdcsService(String serviceLocation) throws MalformedURLException {
		_scraperService = ScraperService.create(serviceLocation);
		_scraperPort = _scraperService.getScraperPort();
	}

	public void insert(String user, String password, Metadata metadata, String rdfXmlPayload) throws InsertException {

		cz.cuni.mff.odcleanstore.engine.ws.scraper.Metadata wsMetadata = new cz.cuni.mff.odcleanstore.engine.ws.scraper.Metadata();

		wsMetadata.setUuid(metadata.getUuid());
		wsMetadata.getPublishedBy().addAll(metadata.getPublishedBy());
		wsMetadata.getSource().addAll(metadata.getSource());
		wsMetadata.getLicense().addAll(metadata.getLicense());
		wsMetadata.setDataBaseUrl(metadata.getDataBaseUrl());
		wsMetadata.setProvenanceBaseUrl(metadata.getProvenanceBaseUrl());
		wsMetadata.setRdfXmlProvenance(metadata.getRdfXmlProvenance());

		try {
			_scraperPort.insert(user, password, wsMetadata, rdfXmlPayload);
		} catch (InsertException_Exception e) {
			throw new InsertException(e.getFaultInfo().getId(), e.getFaultInfo().getMessage(), e.getFaultInfo().getMoreInfo());
		}
	}
}
