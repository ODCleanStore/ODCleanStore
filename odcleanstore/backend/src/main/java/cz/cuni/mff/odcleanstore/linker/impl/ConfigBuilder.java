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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * Class for creating linkage configuration file in Silk-LSL.
 * 
 * @author Tomas Soukup
 */
public class ConfigBuilder {	
	/**
	 * suffix of configuration file
	 */
	private static final String CONFIG_FILENAME = ".xml";	
	/**
	 * Data sources names
	 */
	private static final String CONFIG_SOURCE_A_ID = "sourceA";
	private static final String CONFIG_SOURCE_B_ID = "sourceB";
	/**
	 * Silk-LSL element and attribute names
	 */
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
	private static final String CONFIG_XML_SOURCE_DATASET = "SourceDataset";
	private static final String CONFIG_XML_TARGET_DATASET = "TargetDataset";
	private static final String CONFIG_XML_DATASOURCE = "dataSource";
	private static final String CONFIG_XML_OUTPUT = "Output";
	private static final String CONFIG_XML_TYPE = "type";
	private static final String CONFIG_XML_FILE = "file";
	private static final String CONFIG_XML_SPARQL_ENDPOINT = "sparqlEndpoint";
	
	private static final String TEMP_ENDPOINT = "http://localhost:8890/sparql";
	
	/**
	 * Creates linkage configuration file.
	 * 
	 * File contains RDF prefix definitions, source datasets and linkage rules.
	 * 
	 * @param rawRules list of XML fragments containing linkage rules
	 * @param prefixes list of RDF prefix definitions
	 * @param inputGraph graph to interlink
	 * @param context transformation context
	 * @return file containing linkage configuration
	 * @throws TransformerException when anything fails
	 */
	public static File createLinkConfigFile(List<String> rawRules, List<RDFprefix> prefixes, 
			TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
		
		Document configDoc;
		File configFile;
		try {
			configDoc = createConfigDoc(rawRules, prefixes, inputGraph, context);
			configFile = storeConfigDoc(configDoc, context.getTransformerDirectory(), inputGraph.getGraphId());
		} catch (Exception e) {
			throw new TransformerException(e);
		}
		
		return configFile;
	}
	
	/**
	 * Creates XML document containing linkage configuration.
	 * 
	 * @param rawRules list of XML fragments containing linkage rules
	 * @param prefixes list of RDF prefix definitions
	 * @param inputGraph graph to interlink
	 * @param context transformation context
	 * @return document containing linkage configuration
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Document createConfigDoc(List<String> rawRules, List<RDFprefix> prefixes,
			TransformedGraph inputGraph, TransformationContext context)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document configDoc = builder.newDocument();
		Element root = configDoc.createElement(CONFIG_XML_ROOT);
		configDoc.appendChild(root);
		root.appendChild(createPrefixes(configDoc, prefixes));
		root.appendChild(createSources(configDoc, context.getDirtyDatabaseEndpoint(), 
				context.getCleanDatabaseEndpoint(), inputGraph.getGraphName()));
		root.appendChild(createLinkageRules(configDoc, rawRules, context, inputGraph.getGraphId(), builder));			
		
		return configDoc;
	}
	
	/**
	 * Creates XML element containing RDF prefixes definition.
	 * 
	 * @param doc configuration XML document
	 * @param prefixes list of RDF prefixes
	 * @return XML element containing prefixes definition
	 */
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
	
	/**
	 * Creates XML element containing data sources definition.
	 * 
	 * @param doc configuration XML document
	 * @param dirtyEndpoint SPARQL endpoint to the dirty DB
	 * @param cleanEndpoint SPARQL endpoint to the clean DB
	 * @param graphName name of the graph in dirty DB to be interlinked
	 * @return
	 */
	private static Element createSources(Document doc, SparqlEndpoint dirtyEndpoint, SparqlEndpoint cleanEndpoint, String graphName) {
		Element sourcesElement = doc.createElement(CONFIG_XML_SOURCES);
		
		Element sourceElement = createSource(doc, dirtyEndpoint, graphName);
		sourceElement.setAttribute(CONFIG_XML_ID, CONFIG_SOURCE_A_ID);
		sourcesElement.appendChild(sourceElement);
		
		sourceElement = createSource(doc, cleanEndpoint, null);
		sourceElement.setAttribute(CONFIG_XML_ID, CONFIG_SOURCE_B_ID);
		sourcesElement.appendChild(sourceElement);
		
		return sourcesElement;
	}
	
	/**
	 * Creates XML element containg one data source definition
	 * @param doc configuration XML document
	 * @param endpoint SPARQL endpoint to the data source
	 * @param graphName graph name to be interlinked or null when no graph is specified
	 * @return
	 */
	private static Element createSource(Document doc, SparqlEndpoint endpoint, String graphName) {
		Element sourceElement = doc.createElement(CONFIG_XML_SOURCE);
		sourceElement.setAttribute(CONFIG_XML_TYPE, CONFIG_XML_SPARQL_ENDPOINT);
		
		Element parameterElement = doc.createElement(CONFIG_XML_PARAMETER);
		parameterElement.setAttribute(CONFIG_XML_NAME, CONFIG_XML_ENDPOINT_URI);
		// TODO uncomment when sparql endpoint can be obtained
		//parameterElement.setAttribute(CONFIG_XML_VALUE, endpoint.getUri());
		parameterElement.setAttribute(CONFIG_XML_VALUE, TEMP_ENDPOINT);
		sourceElement.appendChild(parameterElement);
		
		if (graphName != null) {
			parameterElement = doc.createElement(CONFIG_XML_PARAMETER);
			parameterElement.setAttribute(CONFIG_XML_NAME, CONFIG_XML_GRAPH);
			parameterElement.setAttribute(CONFIG_XML_VALUE, graphName);
			sourceElement.appendChild(parameterElement);
		}
		
		return sourceElement;
	}
	
	/**
	 * Creates XML element containing linkage rules.
	 * 
	 * @param doc
	 * @param rawRules list of XML fragments containing linkage rules
	 * @param context transformation context
	 * @param graphId unique ID of the interlinked graph - used for unique filenames
	 * @param builder XML document builder
	 * @return element containing linkage rules
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Element createLinkageRules(Document doc, List<String> rawRules, TransformationContext context, 
			String graphId, DocumentBuilder builder) throws SAXException, IOException {
		Element rulesElement = doc.createElement(CONFIG_XML_LINKAGE_RULES);
		
		for (String rawRule:rawRules) {
			Element ruleElement = builder.parse(new InputSource(new StringReader(rawRule))).getDocumentElement();
			normalizeDatasets(ruleElement);
			updateFileNames(ruleElement, graphId);
			rulesElement.appendChild(doc.importNode(ruleElement, true));
		}
		
		return rulesElement;
	}
	
	/**
	 * Normalizes dataset names so they correspond with dataset definition.
	 * 
	 * @param ruleElement XML element containing linkage rule
	 */
	private static void normalizeDatasets(Element ruleElement) {
		Element sourceElement = (Element)ruleElement.getElementsByTagName(CONFIG_XML_SOURCE_DATASET).item(0);
		sourceElement.setAttribute(CONFIG_XML_DATASOURCE, CONFIG_SOURCE_A_ID);
		Element targetElement = (Element)ruleElement.getElementsByTagName(CONFIG_XML_TARGET_DATASET).item(0);
		targetElement.setAttribute(CONFIG_XML_DATASOURCE, CONFIG_SOURCE_B_ID);
	}
	
	
	/**
	 * Adds uniqe identifier to the output file names to avoid concurrency conflicts.
	 * 
	 * @param ruleElement XML element containing linkage rule
	 * @param graphId unique ID of the interlinked graph - used for unique filenames
	 */
	private static void updateFileNames(Element ruleElement, String graphId) {
		NodeList outputList = ruleElement.getElementsByTagName(CONFIG_XML_OUTPUT);
		for (int i = 0; i < outputList.getLength(); ++i) {
			Element outputElement = (Element)outputList.item(i);
			String type = outputElement.getAttribute(CONFIG_XML_TYPE);
			if (CONFIG_XML_FILE.equals(type)) {
				NodeList paramList = outputElement.getElementsByTagName(CONFIG_XML_PARAMETER);
				for (int j = 0; j < paramList.getLength(); j++) {
					Element paramElement = (Element)paramList.item(j);
					String name = paramElement.getAttribute(CONFIG_XML_NAME);
					if (CONFIG_XML_FILE.equals(name)) {
						String newName = updateFileName(paramElement.getAttribute(CONFIG_XML_VALUE), graphId);
						paramElement.setAttribute(CONFIG_XML_VALUE, newName);
					}
				}
			}
		}
	}
	
	/**
	 * Creates unique file name from original name and unique graph ID.
	 * 
	 * @param name original file name
	 * @param graphId unique graph ID
	 * @return unique file name
	 */
	private static String updateFileName(String name, String graphId) {
		int dotIndex = name.lastIndexOf(".");
		String firstPart = name.substring(0, dotIndex);
		String thirdPart = name.substring(dotIndex);
		return firstPart + graphId + thirdPart;
	}
	

	/**
	 * Stores XML document into a file.
	 * 
	 * Uses graphID to create a uniqe file name.
	 * Stores the file into a directory designated by transformer context
	 * 
	 * @param configDoc XML document containing linkage configuration
	 * @param targetDirectory a directory to store the file to
	 * @param graphId unique graph ID
	 * @return stored file
	 * @throws TransformerFactoryConfigurationError
	 * @throws javax.xml.transform.TransformerException
	 */
	private static File storeConfigDoc(Document configDoc, File targetDirectory, String graphId) 
			throws TransformerFactoryConfigurationError, javax.xml.transform.TransformerException {
		File configFile = new File(targetDirectory, graphId + CONFIG_FILENAME);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(configDoc);
		StreamResult result = new StreamResult(configFile);
			
		transformer.transform(source, result);
				
		return configFile;
	}
}
