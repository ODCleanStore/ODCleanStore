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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cz.cuni.mff.odcleanstore.configuration.ObjectIdentificationConfig;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;
import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.linker.exceptions.InvalidLinkageRuleException;
import cz.cuni.mff.odcleanstore.linker.rules.FileOutput;
import cz.cuni.mff.odcleanstore.linker.rules.Output;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * Class for creating linkage configuration file in Silk-LSL.
 * 
 * @author Tomas Soukup
 */
public class ConfigBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigBuilder.class);
	/**
	 * suffix of configuration file
	 */
	private static final String CONFIG_FILENAME = ".xml";	
	/**
	 * Data sources names
	 */
	private static final String CONFIG_SOURCE_A_ID = "sourceA";
	private static final String CONFIG_SOURCE_B_ID = "sourceB";
	
	private static final String CONFIG_VAR_A = "a";
	private static final String CONFIG_VAR_B = "b";
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
	private static final String CONFIG_XML_LINKAGE_RULE = "Interlink";
	private static final String CONFIG_XML_LINK_TYPE = "LinkType";
	private static final String CONFIG_XML_SOURCE_DATASET = "SourceDataset";
	private static final String CONFIG_XML_TARGET_DATASET = "TargetDataset";
	private static final String CONFIG_XML_DATASOURCE = "dataSource";
	private static final String CONFIG_XML_VAR = "var";
	private static final String CONFIG_XML_RESTRICT_TO = "RestrictTo";
	private static final String CONFIG_XML_FILTER = "Filter";
	private static final String CONFIG_XML_THRESHOLD = "threshold";
	private static final String CONFIG_XML_LIMIT = "limit";
	private static final String CONFIG_XML_OUTPUTS = "Outputs";
	private static final String CONFIG_XML_OUTPUT = "Output";
	private static final String CONFIG_XML_MIN_CONFIDENCE = "minConfidence";
	private static final String CONFIG_XML_MAX_CONFIDENCE = "maxConfidence";
	private static final String CONFIG_XML_TYPE = "type";
	private static final String CONFIG_XML_FILE = "file";
	private static final String CONFIG_XML_FORMAT = "format";
	private static final String CONFIG_XML_SPARQL_ENDPOINT = "sparqlEndpoint";
	private static final String CONFIG_XML_SPARQL_UPDATE = "sparul";
	private static final String CONFIG_XML_URI = "uri";
	private static final String CONFIG_XML_GRAPH_URI = "graphUri";
	private static final String CONFIG_XML_LOGIN = "login";
	private static final String CONFIG_XML_PASSWORD = "password";
	
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
	public static File createLinkConfigFile(List<SilkRule> rules, List<RDFprefix> prefixes, TransformedGraph inputGraph, 
			TransformationContext context, ObjectIdentificationConfig config) throws TransformerException {
		LOG.info("Creating link configuration file.");
		Document configDoc;
		File configFile;
		try {
			configDoc = createConfigDoc(rules, prefixes, inputGraph, config);
			LOG.info("Created link configuration document.");
			configFile = storeConfigDoc(configDoc, context.getTransformerDirectory(), inputGraph.getGraphId());
			LOG.info("Stored link configuration to temporary file {}", configFile.getAbsolutePath());
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
	 * @throws InvalidLinkageRuleException 
	 * @throws DOMException 
	 */
	private static Document createConfigDoc(List<SilkRule> rules, List<RDFprefix> prefixes,
			TransformedGraph inputGraph, ObjectIdentificationConfig config) throws ParserConfigurationException, 
			SAXException, IOException, DOMException, InvalidLinkageRuleException {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document configDoc = builder.newDocument();
		Element root = configDoc.createElement(CONFIG_XML_ROOT);
		configDoc.appendChild(root);
		root.appendChild(createPrefixes(configDoc, prefixes));
		root.appendChild(createSources(configDoc, inputGraph.getGraphName(), config));
		root.appendChild(
				createLinkageRules(configDoc, rules, inputGraph.getGraphId(), builder, config));			
		
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
	 * @param graphName name of the graph in dirty DB to be interlinked
	 * @return
	 */
	private static Element createSources(Document doc, String graphName, ObjectIdentificationConfig config) {
		Element sourcesElement = doc.createElement(CONFIG_XML_SOURCES);
		
		Element sourceElement = createSource(doc, config.getDirtyDBSparqlConnectionCredentials(), graphName);
		sourceElement.setAttribute(CONFIG_XML_ID, CONFIG_SOURCE_A_ID);
		sourcesElement.appendChild(sourceElement);
		
		sourceElement = createSource(doc, config.getCleanDBSparqlConnectionCredentials(), null);
		sourceElement.setAttribute(CONFIG_XML_ID, CONFIG_SOURCE_B_ID);
		sourcesElement.appendChild(sourceElement);
		
		return sourcesElement;
	}
	
	/**
	 * Creates XML element containg one data source definition
	 * @param doc configuration XML document
	 * @param endpointUri SPARQL endpoint to the data source
	 * @param graphName graph name to be interlinked or null when no graph is specified
	 * @return
	 */
	private static Element createSource(Document doc, SparqlEndpointConnectionCredentials credentials, 
			String graphName) {
		Element sourceElement = doc.createElement(CONFIG_XML_SOURCE);
		
		sourceElement.setAttribute(CONFIG_XML_TYPE, CONFIG_XML_SPARQL_ENDPOINT);
		sourceElement.appendChild(createParam(doc, CONFIG_XML_ENDPOINT_URI, credentials.getUrl().toString()));
		
		if (graphName != null) {
			sourceElement.appendChild(createParam(doc, CONFIG_XML_GRAPH, graphName));
		}
		if (credentials.getUsername() != null) {
			sourceElement.appendChild(createParam(doc, CONFIG_XML_LOGIN, credentials.getUsername()));
		}
		if (credentials.getPassword() != null) {
			sourceElement.appendChild(createParam(doc, CONFIG_XML_PASSWORD, credentials.getPassword()));
		}
		
		return sourceElement;
	}
	
	/**
	 * Creates XML element containing linkage rules.
	 * 
	 * @param doc configuration XML document
	 * @param rawRules list of XML fragments containing linkage rules
	 * @param context transformation context
	 * @param graphId unique ID of the interlinked graph - used for unique filenames
	 * @param builder XML document builder
	 * @return element containing linkage rules
	 * @throws SAXException
	 * @throws IOException
	 * @throws InvalidLinkageRuleException 
	 */
	private static Element createLinkageRules(Document doc, List<SilkRule> rules, String graphId, 
			DocumentBuilder builder, ObjectIdentificationConfig config) throws SAXException, IOException,
			InvalidLinkageRuleException {
		Element rulesElement = doc.createElement(CONFIG_XML_LINKAGE_RULES);
		
		for (SilkRule rule: rules) {		
			rulesElement.appendChild(createLinkageRule(doc, rule, graphId, builder, config));
		}
		
		return rulesElement;
	}
	
	private static Element createLinkageRule(Document doc, SilkRule rule, String graphId, 
			DocumentBuilder builder, ObjectIdentificationConfig config) 
					throws SAXException, IOException, DOMException, InvalidLinkageRuleException {
		Element ruleElement = doc.createElement(CONFIG_XML_LINKAGE_RULE);
		ruleElement.setAttribute(CONFIG_XML_ID, rule.getLabel());
		
		ruleElement.appendChild(createTextElement(doc, CONFIG_XML_LINK_TYPE, rule.getLinkType()));
		
		ruleElement.appendChild(createDatasource(
				doc, CONFIG_XML_SOURCE_DATASET, CONFIG_SOURCE_A_ID, CONFIG_VAR_A, rule.getSourceRestriction()));
		ruleElement.appendChild(createDatasource(
				doc, CONFIG_XML_TARGET_DATASET, CONFIG_SOURCE_B_ID, CONFIG_VAR_B, rule.getTargetRestriction()));
		
		Element linkageRuleElement = builder.parse(new InputSource(new StringReader(rule.getLinkageRule()))).
				getDocumentElement();
		ruleElement.appendChild(doc.importNode(linkageRuleElement, true));
		
		ruleElement.appendChild(createFilter(doc, rule.getFilterLimit(), rule.getFilterThreshold()));
		
		ruleElement.appendChild(createOutputs(doc, rule.getOutputs(), graphId, config));
		
		return ruleElement;
	}
	
	private static Element createTextElement(Document doc, String name, String content) {
		Element element = doc.createElement(name);
		element.setTextContent(content);
		return element;
	}
	
	private static Element createDatasource(
			Document doc, String elementName, String sourceId, String variable, String restriction) {
		Element datasourceElement = doc.createElement(elementName);
		datasourceElement.setAttribute(CONFIG_XML_DATASOURCE, sourceId);
		datasourceElement.setAttribute(CONFIG_XML_VAR, variable);
		if (restriction != null) {
			datasourceElement.appendChild(createTextElement(doc, CONFIG_XML_RESTRICT_TO, restriction));
		}
		return datasourceElement;
	}
	
	private static Element createFilter(Document doc, Integer limit, Double threshold) {
		Element filterElement = doc.createElement(CONFIG_XML_FILTER);
		if (limit != null) {
			filterElement.setAttribute(CONFIG_XML_THRESHOLD, threshold.toString());
		}
		if (threshold != null) {
			filterElement.setAttribute(CONFIG_XML_LIMIT, limit.toString());
		}
		return filterElement;
	}
	
	private static Element createOutputs(Document doc, List<Output> outputs, String graphId, 
			ObjectIdentificationConfig config) throws DOMException, InvalidLinkageRuleException {
		Element outputsElement = doc.createElement(CONFIG_XML_OUTPUTS);	
		for (Output output: outputs) {
			outputsElement.appendChild(createOutput(doc, output, graphId, config));
		}		
		return outputsElement;
	}
	
	private static Element createOutput(Document doc, Output output, String graphId, ObjectIdentificationConfig config) 
			throws InvalidLinkageRuleException {
		Element outputElement = doc.createElement(CONFIG_XML_OUTPUT);
		if (output.getMinConfidence() != null) {
			outputElement.setAttribute(CONFIG_XML_MIN_CONFIDENCE, output.getMinConfidence().toString());
		}
		if (output.getMaxConfidence() != null) {
			outputElement.setAttribute(CONFIG_XML_MAX_CONFIDENCE, output.getMaxConfidence().toString());
		}	
		if (output instanceof FileOutput) {
			FileOutput fileOutput = (FileOutput)output;
			outputElement.setAttribute(CONFIG_XML_TYPE, CONFIG_XML_FILE);
			if (fileOutput.getName() == null) {
				throw new InvalidLinkageRuleException("Missing file name (file parameter) in output element.");
			}
			outputElement.appendChild(createParam(doc, CONFIG_XML_FILE, updateFileName(
					fileOutput.getName(), graphId)));
			if (fileOutput.getFormat() == null) {
				throw new InvalidLinkageRuleException("Missing file format parameter in output element.");
			}
			outputElement.appendChild(createParam(doc, CONFIG_XML_FORMAT, fileOutput.getFormat().toLowerCase()));
		} else {
			outputElement.setAttribute(CONFIG_XML_TYPE, CONFIG_XML_SPARQL_UPDATE);
			outputElement.appendChild(createParam(doc, CONFIG_XML_URI, 
					config.getDirtyDBSparqlConnectionCredentials().getUrl().toString()));
			outputElement.appendChild(createParam(doc, CONFIG_XML_GRAPH_URI, 
					config.getLinksGraphURIPrefix().toString() + graphId));
		}		
		return outputElement;
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
	 * Creates parameter element with given name and value.
	 * 
	 * @param doc configuration XML document
	 * @param name name of the parameter
	 * @param value value of the parameter
	 * @return
	 */
	private static Element createParam(Document doc, String name, String value) {
		Element paramElement = doc.createElement(CONFIG_XML_PARAMETER);
		paramElement.setAttribute(CONFIG_XML_NAME, name);
		paramElement.setAttribute(CONFIG_XML_VALUE, value);
		return paramElement;
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
