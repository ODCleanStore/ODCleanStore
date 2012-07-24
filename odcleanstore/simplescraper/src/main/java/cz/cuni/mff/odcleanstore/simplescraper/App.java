package cz.cuni.mff.odcleanstore.simplescraper;

import java.io.FileInputStream;
import java.util.Properties;

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

			sc = new OdcsService("http://localhost:8088/odcleanstore/scraper");
			Metadata metadata = new Metadata();

			props.load(new FileInputStream(args[0]));
			
			metadata.setDataBaseUrl(props.getProperty("databaseurl"));
			metadata.setProvenanceBaseUrl(props.getProperty("provenancebaseurl"));
			metadata.setUuid(props.getProperty("uuid"));

			metadata.getPublishedBy().add(props.getProperty("publishedby"));
			metadata.getSource().add(props.getProperty("source"));
			
			metadata.setPipelineName(props.getProperty("pipelineName"));
			
			FileInputStream fis = new FileInputStream(args[1]);
			byte[] buf = new byte[fis.available()];
			fis.read(buf);
			String payload = new String(buf, "UTF-8");
			
			if (args.length > 2) {
				FileInputStream fis2 = new FileInputStream(args[2]);
				byte[] buf2 = new byte[fis2.available()];
				fis2.read(buf2);
				String provenancePayload = new String(buf2, "UTF-8");
				metadata.setProvenance(provenancePayload);
			}

			sc.insert("scraper", "reparcs", metadata, payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
