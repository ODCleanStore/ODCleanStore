package cz.cuni.mff.odcleanstore.simplescraper;

import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;
import java.util.UUID;

import cz.cuni.mff.odcleanstore.wsclient.Metadata;
import cz.cuni.mff.odcleanstore.wsclient.OdcsService;

/**
 * Simple odcs-inputclient webservice client for testing purposes.
 * @author Petr Jerman
 */
public class App {
	public static void main(String[] args) {
		OdcsService sc;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(args[0]));

			// open webservice on given URL
			sc = new OdcsService("http://localhost:8088/odcleanstore/scraper");
			
			// set metadata from properties file
			Metadata metadata = new Metadata();
			metadata.setDataBaseUrl(new URI(props.getProperty("databaseurl")));
			metadata.setUuid(UUID.fromString(props.getProperty("uuid")));

			metadata.getPublishedBy().add(new URI(props.getProperty("publishedby")));
			metadata.getSource().add(new URI(props.getProperty("source")));
			
			metadata.setPipelineName(props.getProperty("pipelineName"));
			
			// payload in rdfxml format read from file
			FileInputStream fis = new FileInputStream(args[1]);
			byte[] buf = new byte[fis.available()];
			fis.read(buf);
			String payload = new String(buf, "UTF-8");
			
			// provenace payload in rdfxml format read from file
			if (args.length > 2) {
				FileInputStream fis2 = new FileInputStream(args[2]);
				byte[] buf2 = new byte[fis2.available()];
				fis2.read(buf2);
				String provenancePayload = new String(buf2, "UTF-8");
				metadata.setProvenance(provenancePayload);
			}

			// call webservice
			sc.insert("scraper", "reparcs", metadata, payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
