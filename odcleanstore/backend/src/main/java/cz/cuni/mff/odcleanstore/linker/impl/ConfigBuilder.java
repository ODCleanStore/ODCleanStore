package cz.cuni.mff.odcleanstore.linker.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class ConfigBuilder {
	
	private static final String CONFIG_FILENAME = "linkConfig.xml";
	
	private static final String CONFIG_DIRTY_SOURCE_ID = "dirtyDB";
	private static final String CONFIG_CLEAN_SOURCE_ID = "cleanDB";
	
	private static final String CONFIG_XML_ROOT = "Silk";
	private static final String CONFIG_XML_PREFIXES = "Prefixes";
	private static final String CONFIG_XML_PREFIX = "Prefix";
	private static final String CONFIG_XML_ID = "id";
	private static final String CONFIG_XML_PREFIX_NAMESPACE = "namespace";
	private static final String CONFIG_XML_SOURCES = "DataSources";
	private static final String CONFIG_XML_SOURCE = "DataSource";
	private static final String CONFIG_XML_PARAMETER = "Param";
	private static final String CONFIG_XML_NAME = "name";
	private static final String CONFIG_XML_VALUE = "value";
	private static final String CONFIG_XML_ENDPOINT_URI = "endpointURI";
	private static final String CONFIG_XML_GRAPH = "graph";
	private static final String CONFIG_XML_LINKAGE_RULES = "Interlinks";
	
	public static File createLinkConfigFile(List<String> rawRules, List<RDFprefix> prefixes, 
			TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
		
		Document configDoc;
		File configFile;
		try {
			configDoc = createConfigDoc(rawRules, prefixes, inputGraph, context);
			configFile = storeConfigDoc(configDoc, context.getTransformerDirectory());
		} catch (Exception e) {
			throw new TransformerException(e);
		}
		
		return configFile;
	}
	
	private static Document createConfigDoc(List<String> rawRules, List<RDFprefix> prefixes,
			TransformedGraph inputGraph, TransformationContext context)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document configDoc = builder.newDocument();
		Element root = configDoc.createElement(CONFIG_XML_ROOT);
		root.appendChild(createPrefixes(configDoc, prefixes));
		root.appendChild(createSources(configDoc, context.getDirtyDatabaseEndpoint(), 
				context.getCleanDatabaseEndpoint(), inputGraph.getGraphName()));
		root.appendChild(createLinkageRules(configDoc, rawRules, context, builder));			
		
		return configDoc;
	}
	
	private static Element createPrefixes(Document doc, List<RDFprefix> prefixes) {
		Element prefixesElement = doc.createElement(CONFIG_XML_PREFIXES);
		
		for (RDFprefix prefix:prefixes) {
			Element prefixElement = doc.createElement(CONFIG_XML_PREFIX);;
			prefixElement.setAttribute(CONFIG_XML_ID, prefix.getPrefixId());
			prefixElement.setAttribute(CONFIG_XML_PREFIX_NAMESPACE, prefix.getNamespace());
			
			prefixesElement.appendChild(prefixElement);
		}
		
		return prefixesElement;
	}
	
	private static Element createSources(Document doc, SparqlEndpoint dirtyEndpoint, SparqlEndpoint cleanEndpoint, String graphName) {
		Element sourcesElement = doc.createElement(CONFIG_XML_SOURCES);
		
		Element sourceElement = createSource(doc, dirtyEndpoint, graphName);
		sourceElement.setAttribute(CONFIG_XML_ID, CONFIG_DIRTY_SOURCE_ID);
		sourcesElement.appendChild(sourceElement);
		
		sourceElement = createSource(doc, cleanEndpoint, null);
		sourceElement.setAttribute(CONFIG_XML_ID, CONFIG_CLEAN_SOURCE_ID);
		sourcesElement.appendChild(sourceElement);
		
		return sourcesElement;
	}
	
	private static Element createSource(Document doc, SparqlEndpoint endpoint, String graphName) {
		Element sourceElement = doc.createElement(CONFIG_XML_SOURCE);
		
		Element parameterElement = doc.createElement(CONFIG_XML_PARAMETER);
		parameterElement.setAttribute(CONFIG_XML_NAME, CONFIG_XML_ENDPOINT_URI);
		parameterElement.setAttribute(CONFIG_XML_VALUE, endpoint.getUri());
		sourceElement.appendChild(parameterElement);
		
		if (graphName != null) {
			parameterElement = doc.createElement(CONFIG_XML_PARAMETER);
			parameterElement.setAttribute(CONFIG_XML_NAME, CONFIG_XML_GRAPH);
			parameterElement.setAttribute(CONFIG_XML_VALUE, graphName);
			sourceElement.appendChild(parameterElement);
		}
		
		return sourceElement;
	}
	
	private static Element createLinkageRules(Document doc, List<String> rawRules, TransformationContext context, 
			DocumentBuilder builder) throws SAXException, IOException {
		Element rulesElement = doc.createElement(CONFIG_XML_LINKAGE_RULES);
		
		for (String rawRule:rawRules) {
			Element ruleElement = builder.parse(new InputSource(new StringReader(rawRule))).getDocumentElement();
			rulesElement.appendChild(ruleElement);
		}
		
		//TODO synchronize dataSource IDs between rules and sources
		
		return rulesElement;
	}

	private static File storeConfigDoc(Document configDoc, File targetDirectory) 
			throws TransformerFactoryConfigurationError, javax.xml.transform.TransformerException {
		File configFile = new File(targetDirectory, CONFIG_FILENAME);
		
		Transformer transformer;
		
		transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(configDoc);
		StreamResult result = new StreamResult(configFile);
			
		transformer.transform(source, result);
				
		return configFile;
	}
}
