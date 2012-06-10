package cz.cuni.mff.odcleanstore.linker.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class ConfigBuilderTest {
	@Test
	public void testCreateConfigFile() throws TransformerException, ParserConfigurationException, SAXException, IOException {
		List<String> rules = new ArrayList<String>();
		rules.add("<Interlink><SourceDataset/><TargetDataset/><LinkageRule><SomeContent/></LinkageRule></Interlink>");
		rules.add("<Interlink><SourceDataset/><TargetDataset/><LinkageRule><OtherContent/></LinkageRule></Interlink>");
		rules.add("<Interlink><SourceDataset/><TargetDataset/><LinkageRule><ThirdContent/></LinkageRule></Interlink>");
		
		List<RDFprefix> prefixes = new ArrayList<RDFprefix>();
		prefixes.add(new RDFprefix("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
		prefixes.add(new RDFprefix("rdfs:", "http://www.w3.org/2000/01/rdf-schema#"));
				
		TransformedGraph graph = new TransformedGraphMock("http://odcs.mff.cuni.cz/transformedGraph");
		TransformationContext context = new TransformationContextMock("target/linkerTest");
		String linksGraphName = "http://odcs.mff.cuni.cz/namedGraph/generatedLinks/";
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		File configFile = ConfigBuilder.createLinkConfigFile(rules, prefixes, graph, 
				context, linksGraphName);
		Document configDoc = builder.parse(configFile);
		
		File expectedFile = new File("src/test/resources/expectedLinkConfig.xml");
		Document expectedDoc = builder.parse(expectedFile);
		
		assertTrue(configDoc.isEqualNode(expectedDoc));
	}
}
