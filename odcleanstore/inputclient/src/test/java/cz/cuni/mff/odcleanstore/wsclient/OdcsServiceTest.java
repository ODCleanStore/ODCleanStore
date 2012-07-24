package cz.cuni.mff.odcleanstore.wsclient;

import java.io.IOException;

import org.junit.Test;

/**
 *  @author Petr Jerman
 */
public class OdcsServiceTest {

	@Test
	public void testOdcsService() throws IOException, InsertException {
		/*OdcsService sc = new OdcsService("http://localhost:8088/odcleanstore/scraper");

		Metadata metadata = new Metadata();

		metadata.setDataBaseUrl("prd");
		metadata.setProvenanceBaseUrl("prd");
		metadata.setUuid("ff714b5e-1572-4317-b9ea-dd22a91787d023");
		
		String provenance = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dcterm=\"http://purl.org/dc/terms/\" xmlns:vcard=\"http://www.w3.org/2006/vcard/ns#\" xmlns:sindicevocab=\"http://vocab.sindice.net/\">"
				+ "<rdf:Description rdf:about=\"http://xhinker.com/sioc.axd\">" + "  <dcterm:title>SIOC</dcterm:title>" + "  <dcterm:format>application/rdf+xml</dcterm:format>" + "</rdf:Description>"
				+ "</rdf:RDF>";
		
		metadata.setProvenance(provenance);

		metadata.getPublishedBy().add("gogo");
		metadata.getSource().add("gogo-source");

		String payload = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dcterm=\"http://purl.org/dc/terms/\" xmlns:vcard=\"http://www.w3.org/2006/vcard/ns#\" xmlns:sindicevocab=\"http://vocab.sindice.net/\">"
				+ "<rdf:Description rdf:about=\"http://xhinker.com/sioc.axd\">" + "  <dcterm:title>SIOC</dcterm:title>" + "  <dcterm:format>application/rdf+xml</dcterm:format>" + "</rdf:Description>"
				+ "<rdf:Description rdf:about=\"http://xhinker.com/foaf.axd\">" + "  <dcterm:title>FOAF</dcterm:title>" + "  <dcterm:format>application/rdf+xml</dcterm:format>" + "</rdf:Description>"
				+ "<rdf:Description rdf:about=\"http://xhinker.com/post/BlogEngineEnable-Nested-Comments-.aspx\">"
				+ "  <dcterm:title xml:lang=\"en\">Andrew Zhu | Xhinker | [BlogEngine]Enable Nested Comments</dcterm:title>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911003\">" + "  <vcard:fn>hackthestockmarket</vcard:fn>" + "  <vcard:n rdf:nodeID=\"node16prkt4qux911004\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911004\">" + "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#Name\"/>"
				+ "  <vcard:given-name>hackthestockmarket</vcard:given-name>" + "  <vcard:family-name>hackthestockmarket</vcard:family-name>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911003\">" + "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#VCard\"/>"
				+ "  <vcard:photo rdf:resource=\"http://www.gravatar.com/avatar/e8116d90bcd0d07f225f9b9081d310c4.jpg?s=32&amp;d=identicon\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911005\">" + "  <vcard:fn>Auto Shipping Rates</vcard:fn>" + "  <vcard:n rdf:nodeID=\"node16prkt4qux911006\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911006\">" + "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#Name\"/>" + "  <vcard:given-name>Auto</vcard:given-name>"
				+ "  <vcard:family-name>Rates</vcard:family-name>" + "</rdf:Description>" + "<rdf:Description rdf:nodeID=\"node16prkt4qux911005\">"
				+ "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#VCard\"/>" + "  <vcard:url rdf:resource=\"http://www.autoshippingfinder.com/\"/>"
				+ "  <vcard:photo rdf:resource=\"http://www.gravatar.com/avatar/c39db952aedd6bb467445874e750618f.jpg?s=32&amp;d=identicon\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911007\">" + "  <vcard:fn>Chris</vcard:fn>" + "  <vcard:n rdf:nodeID=\"node16prkt4qux911008\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911008\">" + "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#Name\"/>" + "  <vcard:given-name>Chris</vcard:given-name>"
				+ "  <vcard:family-name>Chris</vcard:family-name>" + "</rdf:Description>" + "<rdf:Description rdf:nodeID=\"node16prkt4qux911007\">"
				+ "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#VCard\"/>" + "  <vcard:url rdf:resource=\"http://www.clouds.de/\"/>"
				+ "  <vcard:photo rdf:resource=\"http://www.gravatar.com/avatar/9a394d797147cec2768790cc0aef6c91.jpg?s=32&amp;d=identicon\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911009\">" + "  <vcard:fn>it news</vcard:fn>" + "  <vcard:n rdf:nodeID=\"node16prkt4qux911010\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911010\">" + "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#Name\"/>" + "  <vcard:given-name>it</vcard:given-name>"
				+ "  <vcard:family-name>news</vcard:family-name>" + "</rdf:Description>" + "<rdf:Description rdf:nodeID=\"node16prkt4qux911009\">"
				+ "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#VCard\"/>" + "  <vcard:url rdf:resource=\"http://www.emdadblog.com/\"/>"
				+ "  <vcard:photo rdf:resource=\"http://www.gravatar.com/avatar/6ae2202660f477daaf1256a182b51a09.jpg?s=32&amp;d=identicon\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911011\">" + "  <vcard:fn>paintless remouval</vcard:fn>" + "  <vcard:n rdf:nodeID=\"node16prkt4qux911012\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911012\">" + "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#Name\"/>" + "  <vcard:given-name>paintless</vcard:given-name>"
				+ "  <vcard:family-name>remouval</vcard:family-name>" + "</rdf:Description>" + "<rdf:Description rdf:nodeID=\"node16prkt4qux911011\">"
				+ "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#VCard\"/>" + "  <vcard:url rdf:resource=\"http://www.nodents.com/\"/>"
				+ " <vcard:photo rdf:resource=\"http://www.gravatar.com/avatar/39896af7afdf4bf058d145f40bd16fb2.jpg?s=32&amp;d=identicon\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911013\">" + "  <vcard:fn>cloud provider</vcard:fn>" + "  <vcard:n rdf:nodeID=\"node16prkt4qux911014\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:nodeID=\"node16prkt4qux911014\">" + "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#Name\"/>" + "  <vcard:given-name>cloud</vcard:given-name>"
				+ "  <vcard:family-name>provider</vcard:family-name>" + "</rdf:Description>" + "<rdf:Description rdf:nodeID=\"node16prkt4qux911013\">"
				+ "  <rdf:type rdf:resource=\"http://www.w3.org/2006/vcard/ns#VCard\"/>" + "  <vcard:url rdf:resource=\"http://www.clouds.de/alle-anbieter\"/>"
				+ "  <vcard:photo rdf:resource=\"http://www.gravatar.com/avatar/75e0ca4e5e31941dacb5ba517c394075.jpg?s=32&amp;d=identicon\"/>" + "</rdf:Description>"
				+ "<rdf:Description rdf:about=\"http://example.com\">" + "  <sindicevocab:date>2012-04-30T15:09:57+01:00</sindicevocab:date>"
				+ "  <sindicevocab:size rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">54</sindicevocab:size>" + "</rdf:Description> " + "</rdf:RDF>";

		sc.insert("scraper", "reparcs", metadata, payload);*/
	}
}
