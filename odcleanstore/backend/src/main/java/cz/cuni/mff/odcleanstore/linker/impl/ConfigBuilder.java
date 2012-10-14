package cz.cuni.mff.odcleanstore.linker.impl;

import cz.cuni.mff.odcleanstore.configuration.ObjectIdentificationConfig;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;
import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.linker.exceptions.InvalidLinkageRuleException;
import cz.cuni.mff.odcleanstore.linker.rules.FileOutput;
import cz.cuni.mff.odcleanstore.linker.rules.Output;
import cz.cuni.mff.odcleanstore.linker.rules.OutputType;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;
import cz.cuni.mff.odcleanstore.shared.SerializationLanguage;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final String CONFIG_XML_INTERLINKS = "Interlinks";
	private static final String CONFIG_XML_INTERLINK = "Interlink";
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
	private static final String CONFIG_XML_LINKAGE_RULE = "LinkageRule";
	private static final String CONFIG_XML_ALIGNMENT = "alignment";
	private static final String CONFIG_XML_INPUT = "Input";
	private static final String CONFIG_XML_TRANSFORM_INPUT = "TransformInput";
	private static final String CONFIG_XML_COMPARE = "Compare";
	private static final String CONFIG_XML_AGGREGATE = "Aggregate";

	private static final BigDecimal MIN_CONFIDENCE = BigDecimal.ZERO;
	private static final BigDecimal MAX_CONFIDENCE = BigDecimal.valueOf(1000);

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
	public static File createLinkConfigFile(List<SilkRule> rules, List<RDFprefix> prefixes,
			TransformedGraph inputGraph, TransformationContext context, ObjectIdentificationConfig config,
			boolean linkWithinGraph) throws TransformerException {
		LOG.debug("Creating link configuration file.");
		Document configDoc;
		File configFile;
		try {
			configDoc = createConfigDoc(rules, prefixes, inputGraph.getGraphName(), inputGraph.getGraphId(), config,
					context.getTransformerDirectory(), linkWithinGraph);
			LOG.debug("Created link configuration document.");
			configFile = storeConfigDoc(configDoc, context.getTransformerDirectory(), inputGraph.getGraphId());
			LOG.debug("Stored link configuration to temporary file {}", configFile.getAbsolutePath());
		} catch (Exception e) {
			throw new TransformerException(e);
		}

		return configFile;
	}

	/**
	 * Parses given link configuration file.
	 *
	 * @param inputFile input stream to parse
	 * @return parsed rule
	 * @throws javax.xml.transform.TransformerException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static SilkRule parseRule(InputStream input)
			throws javax.xml.transform.TransformerException, ParserConfigurationException, SAXException, IOException {
		LOG.info("Parsing Silk linkage rule.");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(input);

		SilkRule rule = new SilkRule();

		NodeList nodeList = doc.getElementsByTagName(CONFIG_XML_INTERLINK);
		if (nodeList.getLength() < 1) {
			return rule;
		}
		Element ruleElement = (Element)nodeList.item(0);

		rule.setLabel(parseLabel(ruleElement));
		rule.setLinkType(parseLinkType(ruleElement));
		rule.setSourceRestriction(parseRestriction(ruleElement, CONFIG_XML_SOURCE_DATASET));
		rule.setTargetRestriction(parseRestriction(ruleElement, CONFIG_XML_TARGET_DATASET));
		rule.setLinkageRule(parseLinkageRule(ruleElement));
		Element filterElement = getFirstChild(ruleElement, CONFIG_XML_FILTER);
		if (filterElement != null) {
			rule.setFilterLimit(parseFilterLimit(filterElement));
			rule.setFilterThreshold(parseFilterThreshold(filterElement));
		}		
		rule.setOutputs(parseOutputs(ruleElement));

		return rule;
	}

	private static String parseLabel(Element ruleElement) {
		String label = ruleElement.getAttribute(CONFIG_XML_ID);
		if ((label == null) || label.isEmpty()) {
			return null;
		}
		return label;
	}

	private static String parseLinkType(Element parentElement) {
		Element typeElement = getFirstChild(parentElement, CONFIG_XML_LINK_TYPE);
		if (typeElement == null) {
			return null;
		}
		return typeElement.getTextContent();
	}

	private static String parseRestriction(Element parentElement, String subElementName) {
		Element datasetElement = getFirstChild(parentElement, subElementName);
		if (datasetElement == null) {
			return null;
		}
		Element restrictElement = getFirstChild(datasetElement, CONFIG_XML_RESTRICT_TO);
		if (restrictElement == null) {
			return null;
		}
		return restrictElement.getTextContent();
	}

	private static String parseLinkageRule(Element parentElement) throws javax.xml.transform.TransformerException {
		Element linkageRuleElement = getFirstChild(parentElement, CONFIG_XML_LINKAGE_RULE);
		if (linkageRuleElement == null) {
			return null;
		}
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(linkageRuleElement), new StreamResult(buffer));
		return buffer.toString();
	}

	private static Integer parseFilterLimit(Element filterElement) {
		String limit = filterElement.getAttribute(CONFIG_XML_LIMIT);
		if ((limit == null) || limit.isEmpty()) {
			return null;
		}
		return Integer.parseInt(limit);
	}

	private static BigDecimal parseFilterThreshold(Element filterElement) {
		String threshold = filterElement.getAttribute(CONFIG_XML_THRESHOLD);
		if ((threshold == null) || threshold.isEmpty()) {
			return null;
		}
		return new BigDecimal(threshold);
	}

	private static List<Output> parseOutputs(Element parentElement) {
		List<Output> outputs = new ArrayList<Output>();
		Element outputsElement = getFirstChild(parentElement, CONFIG_XML_OUTPUTS);
		if (outputsElement != null) {
			NodeList nodeList = outputsElement.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					outputs.add(parseOutput((Element)nodeList.item(i)));
				}			
			}
		}	
		return outputs;
	}

	private static Output parseOutput(Element outputElement) {
		Output output;
		String type = outputElement.getAttribute(CONFIG_XML_TYPE);
		if ((type != null) && (OutputType.FILE.toString().equals(type.toUpperCase()))) {
			FileOutput fileOutput = new FileOutput();
			NodeList paramList = outputElement.getElementsByTagName(CONFIG_XML_PARAMETER);
			for (int i = 0; i < paramList.getLength(); i++) {
				Element paramElement = (Element)paramList.item(i);
				String paramName = paramElement.getAttribute(CONFIG_XML_NAME);
				if (CONFIG_XML_FILE.equals(paramName)) {
					fileOutput.setName(paramElement.getAttribute(CONFIG_XML_VALUE));
				} else if (CONFIG_XML_FORMAT.equals(paramName)) {
					fileOutput.setFormat(paramElement.getAttribute(CONFIG_XML_VALUE));
				}
			}
			output = fileOutput;
		} else {
			output = new Output();
		}

		output.setMinConfidence(parseBigDecimal(outputElement.getAttribute(CONFIG_XML_MIN_CONFIDENCE)));
		output.setMaxConfidence(parseBigDecimal(outputElement.getAttribute(CONFIG_XML_MAX_CONFIDENCE)));

		return output;
	}

	private static BigDecimal parseBigDecimal(String input) {
		if ((input == null) || input.isEmpty()) {
			return null;
		}
		return new BigDecimal(input);
	}

	private static Element getFirstChild(Element parentElement, String tagName) {
		NodeList nodeList = parentElement.getElementsByTagName(tagName);
		if (nodeList.getLength() < 1) {
			return null;
		} else {
			return (Element)nodeList.item(0);
		}
	}

	private static Element getFirstElement(Document doc, String tagName) {
		NodeList nodeList = doc.getElementsByTagName(tagName);
		if (nodeList.getLength() < 1) {
			return null;
		} else {
			return (Element)nodeList.item(0);
		}
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
	private static Document createConfigDoc(List<SilkRule> rules, List<RDFprefix> prefixes, String graphName,
			String fileId, ObjectIdentificationConfig config, File transformerDirectory, boolean linkWithinGraph)
					throws ParserConfigurationException, SAXException, IOException, DOMException,
					InvalidLinkageRuleException {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document configDoc = builder.newDocument();
		Element root = configDoc.createElement(CONFIG_XML_ROOT);
		configDoc.appendChild(root);
		root.appendChild(createPrefixes(configDoc, prefixes));
		root.appendChild(createSources(configDoc, graphName, config));
		root.appendChild(createLinkageRules(
				configDoc, rules, fileId, builder, config, transformerDirectory, linkWithinGraph));

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
			DocumentBuilder builder, ObjectIdentificationConfig config, File transformerDirectory,
			boolean linkWithinGraph) throws SAXException, IOException, InvalidLinkageRuleException {
		Element rulesElement = doc.createElement(CONFIG_XML_INTERLINKS);
		for (SilkRule rule: rules) {
			rulesElement.appendChild(createLinkageRule(doc, rule, graphId, builder, config, transformerDirectory, false));
			if (linkWithinGraph) {
				rulesElement.appendChild(
						createLinkageRule(doc, rule, graphId, builder, config, transformerDirectory, true));
			}
		}

		return rulesElement;
	}

	private static Element createLinkageRule(Document doc, SilkRule rule, String graphId, DocumentBuilder builder,
			ObjectIdentificationConfig config, File transformerDirectory, boolean linkWithinGraph)
					throws SAXException, IOException, DOMException, InvalidLinkageRuleException {
		Element ruleElement = doc.createElement(CONFIG_XML_INTERLINK);
		String id = linkWithinGraph ? "id_within_" + rule.getId() : "id_" + rule.getId();
		ruleElement.setAttribute(CONFIG_XML_ID, id);

		ruleElement.appendChild(createTextElement(doc, CONFIG_XML_LINK_TYPE, rule.getLinkType()));

		ruleElement.appendChild(createDatasource(
				doc, CONFIG_XML_SOURCE_DATASET, CONFIG_SOURCE_A_ID, CONFIG_VAR_A, rule.getSourceRestriction()));
		String targetDatasourceId = linkWithinGraph ? CONFIG_SOURCE_A_ID : CONFIG_SOURCE_B_ID;
		ruleElement.appendChild(createDatasource(
				doc, CONFIG_XML_TARGET_DATASET, targetDatasourceId, CONFIG_VAR_B, rule.getTargetRestriction()));

		Element linkageRuleElement = builder.parse(new InputSource(new StringReader(rule.getLinkageRule()))).
				getDocumentElement();
		filterIDs(linkageRuleElement);

		ruleElement.appendChild(doc.importNode(linkageRuleElement, true));

		ruleElement.appendChild(createFilter(doc, rule.getFilterLimit(), rule.getFilterThreshold()));

		ruleElement.appendChild(createOutputs(doc, rule.getOutputs(), graphId, config, transformerDirectory));

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

	private static Element createFilter(Document doc, Integer limit, BigDecimal threshold) {
		Element filterElement = doc.createElement(CONFIG_XML_FILTER);
		if (threshold != null) {
			filterElement.setAttribute(CONFIG_XML_THRESHOLD, threshold.toString());
		}
		if (limit != null) {
			filterElement.setAttribute(CONFIG_XML_LIMIT, limit.toString());
		}
		return filterElement;
	}

	private static Element createOutputs(Document doc, List<Output> outputs, String graphId,
			ObjectIdentificationConfig config, File transformerDirectory)
					throws DOMException, InvalidLinkageRuleException {
		Element outputsElement = doc.createElement(CONFIG_XML_OUTPUTS);
		for (Output output: outputs) {
			outputsElement.appendChild(createOutput(doc, output, graphId, config, transformerDirectory));
		}
		return outputsElement;
	}

	private static Element createOutput(Document doc, Output output, String graphId,
			ObjectIdentificationConfig config, File transformerDirectory) throws InvalidLinkageRuleException {
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
					fileOutput.getName(), graphId, transformerDirectory)));
			if (fileOutput.getFormat() == null) {
				throw new InvalidLinkageRuleException("Missing file format parameter in output element.");
			}
			outputElement.appendChild(createParam(doc, CONFIG_XML_FORMAT, fileOutput.getFormat().toLowerCase()));
		} else {
			outputElement.setAttribute(CONFIG_XML_TYPE, CONFIG_XML_SPARQL_UPDATE);
			outputElement.appendChild(createParam(doc, CONFIG_XML_URI,
					config.getDirtyDBSparqlConnectionCredentials().getUrl().toString()));
			outputElement.appendChild(createParam(doc, CONFIG_XML_GRAPH_URI,
			        ODCSInternal.generatedLinksGraphUriPrefix + graphId));
		}
		return outputElement;
	}

	/**
	 * Creates unique file name from original name and unique graph ID.
	 *
	 * @param name original file name
	 * @param graphId unique graph ID
	 * @param transformerDirectory transformer working directory
	 * @return unique file name
	 */
	private static String updateFileName(String name, String graphId, File transformerDirectory) {
	    Pattern pattern = Pattern.compile("(.*:)?(.*)(\\..*)?");
	    Matcher matcher = pattern.matcher(name);

	    String namePart = "";
        String extension = "";
	    if (matcher.matches()) {
	        // CHECKSTYLE:OFF
	        if (matcher.group(2) != null) {
	            namePart = matcher.group(2);
	        }
	        if (matcher.group(3) != null) {
	            extension = matcher.group(3);
	        }
	        // CHECKSTYLE:OFF
	    }
	    return new File(transformerDirectory, namePart + graphId + "."  + extension).getAbsolutePath();
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
	 * @param graphId unique file ID
	 * @return stored file
	 * @throws TransformerFactoryConfigurationError
	 * @throws javax.xml.transform.TransformerException
	 */
	private static File storeConfigDoc(Document configDoc, File targetDirectory, String fileId)
			throws TransformerFactoryConfigurationError, javax.xml.transform.TransformerException {
		File configFile = new File(targetDirectory, fileId + CONFIG_FILENAME);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(configDoc);
		StreamResult result = new StreamResult(configFile);

		transformer.transform(source, result);

		return configFile;
	}

	public static File createDebugLinkConfigFile(SilkRule rule, List<RDFprefix> prefixes,
			TransformationContext context, ObjectIdentificationConfig config, String inputFileName,
			String resultFileName, SerializationLanguage language) throws TransformerException {
		LOG.info("Creating debug link configuration file.");
		Document configDoc;
		File configFile;
		List<SilkRule> rules = new ArrayList<SilkRule>();
		rules.add(rule);
		String randomId = UUID.randomUUID().toString();
		try {
			configDoc = createConfigDoc(rules, prefixes, null, randomId, config,
					context.getTransformerDirectory(), config.isLinkWithinGraph());
			changeSourceToFile(configDoc, inputFileName, language);
			redirectOutputToFile(configDoc, resultFileName, rule.getOutputs());
			LOG.info("Created link configuration document.");
			configFile = storeConfigDoc(configDoc, context.getTransformerDirectory(), randomId);
			LOG.info("Stored link configuration to temporary file {}", configFile.getAbsolutePath());
		} catch (Exception e) {
			throw new TransformerException(e);
		}

		return configFile;
	}

	private static void redirectOutputToFile(Document doc, String resultFileName, List<Output> outputs) {
		Element debugOutputsElement = createDebugOutputsElement(doc, resultFileName, outputs);
		Element ruleElement = getFirstElement(doc, CONFIG_XML_INTERLINK);
		Element outputsElement = getFirstChild(ruleElement, CONFIG_XML_OUTPUTS);
		ruleElement.replaceChild(debugOutputsElement, outputsElement);
	}

	private static Element createDebugOutputsElement(Document doc, String resultFileName, List<Output> outputs) {
		Element outputElement = doc.createElement(CONFIG_XML_OUTPUT);
		BigDecimal minConfidence = getMinConfidence(outputs);
		if (minConfidence != null) {
			outputElement.setAttribute(CONFIG_XML_MIN_CONFIDENCE, minConfidence.toString());
		}
		BigDecimal maxConfidence = getMaxConfidence(outputs);
		if (maxConfidence != null) {
			outputElement.setAttribute(CONFIG_XML_MAX_CONFIDENCE, maxConfidence.toString());
		}
		outputElement.setAttribute(CONFIG_XML_TYPE, CONFIG_XML_FILE);
		outputElement.appendChild(createParam(doc, CONFIG_XML_FILE, resultFileName));
		outputElement.appendChild(createParam(doc, CONFIG_XML_FORMAT, CONFIG_XML_ALIGNMENT));

		Element outputsElement = doc.createElement(CONFIG_XML_OUTPUTS);
		outputsElement.appendChild(outputElement);

		return outputsElement;
	}

	private static BigDecimal getMinConfidence(List<Output> outputs) {
		BigDecimal min = MAX_CONFIDENCE;
		for (Output output: outputs) {
			BigDecimal confidence = output.getMinConfidence();
			if (confidence == null) {
				return null;
			} else if (confidence.compareTo(min) == -1) {
				min = confidence;
			}
		}
		return min;
	}

	private static BigDecimal getMaxConfidence(List<Output> outputs) {
		BigDecimal max = MIN_CONFIDENCE;
		for (Output output: outputs) {
			BigDecimal confidence = output.getMaxConfidence();
			if (confidence == null) {
				return null;
			} else if (confidence.compareTo(max) == 1) {
				max = confidence;
			}
		}
		return max;
	}

	private static void changeSourceToFile(Document doc, String inputFileName, SerializationLanguage language) {
		Element newSourceElement = createSourceElement(doc, inputFileName, language);
		Element sources = getFirstElement(doc, CONFIG_XML_SOURCES);
		Element oldSourceElement = getFirstChild(sources, CONFIG_XML_SOURCE);
		sources.replaceChild(newSourceElement, oldSourceElement);
	}
	
	private static Element createSourceElement(Document doc, String inputFileName, SerializationLanguage language) {
		Element sourceElement = doc.createElement(CONFIG_XML_SOURCE);

		sourceElement.setAttribute(CONFIG_XML_TYPE, CONFIG_XML_FILE);
		sourceElement.setAttribute(CONFIG_XML_ID, CONFIG_SOURCE_A_ID);
		sourceElement.appendChild(createParam(doc, CONFIG_XML_FILE, inputFileName));
		sourceElement.appendChild(createParam(doc, CONFIG_XML_FORMAT, language.toString()));

		return sourceElement;
	}

	private static void filterIDs(Element element) {
		filterIDs(element.getElementsByTagName(CONFIG_XML_INPUT));
		filterIDs(element.getElementsByTagName(CONFIG_XML_TRANSFORM_INPUT));
		filterIDs(element.getElementsByTagName(CONFIG_XML_COMPARE));
		filterIDs(element.getElementsByTagName(CONFIG_XML_AGGREGATE));
	}

	private static void filterIDs(NodeList nodeList) {
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Element element = (Element)nodeList.item(i);
			element.removeAttribute(CONFIG_XML_ID);
		}
	}
}
