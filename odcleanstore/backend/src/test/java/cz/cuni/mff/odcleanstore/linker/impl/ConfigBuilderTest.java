package cz.cuni.mff.odcleanstore.linker.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.ObjectIdentificationConfig;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.linker.rules.Output;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class ConfigBuilderTest {
	
	private static final String GROUP_NAME = "object_identification";
	
	@Test
	public void testCreateConfigFile() throws TransformerException, ParserConfigurationException, SAXException, 
	IOException, ConfigurationException {
		List<SilkRule> rules = new ArrayList<SilkRule>();
		SilkRule rule = new SilkRule();
		rule.setLabel("testRule");
		rule.setLinkType("owl:sameAs");
		rule.setSourceRestriction("?a rdf:type foo .");
		rule.setTargetRestriction("?b rdf:type poo .");
		rule.setFilterLimit(5);
		rule.setFilterThreshold(new BigDecimal("0.9"));
		rule.setLinkageRule("<Interlink>ruleContent</Interlink>");
		
		List<Output> outputs = new ArrayList<Output>();
		Output output = new Output();
		output.setMinConfidence(new BigDecimal("0.95"));
		output.setMaxConfidence(new BigDecimal("0.98"));
		outputs.add(output);
		rule.setOutputs(outputs);
		
		rules.add(rule);
		
		List<RDFprefix> prefixes = new ArrayList<RDFprefix>();
		prefixes.add(new RDFprefix("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
		prefixes.add(new RDFprefix("rdfs:", "http://www.w3.org/2000/01/rdf-schema#"));
				
		TransformedGraph graph = new TransformedGraphMock("http://odcs.mff.cuni.cz/transformedGraph");
		TransformationContext context = new TransformationContextMock("target/linkerTest");
		
		Properties properties = Mockito.mock(Properties.class);
		Mockito.when(properties.getProperty(GROUP_NAME + ".links_graph_uri_prefix")).thenReturn("http://www.seznam.cz");
        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_sparql_endpoint_url")).thenReturn("http://www.google.cz");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_url")).thenReturn("http://www.yahoo.com");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_username")).thenReturn("Pepa");
	    Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_password")).thenReturn("heslo");
	    ObjectIdentificationConfig config = ObjectIdentificationConfig.load(properties);
		
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		File configFile = ConfigBuilder.createLinkConfigFile(rules, prefixes, graph, 
				context, config);
		Document configDoc = builder.parse(configFile);
		
		File expectedFile = new File("src/test/resources/expectedLinkConfig.xml");
		Document expectedDoc = builder.parse(expectedFile);
		
		assertTrue(configDoc.isEqualNode(expectedDoc));
	}
}
