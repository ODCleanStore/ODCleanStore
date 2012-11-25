package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.vocabulary.XPathFunctions;

/**
 * A rule used by Data Normalization Rule Generation from Ontologies
 *
 * converts numbers, dates... to string when the property is expected to have only string values
 * this way subsequent queries do not need to care about stringification
 *
 * @author Jakub Daniel
 */
public class DataNormalizationStringRule extends DataNormalizationRule
{
	private static final long serialVersionUID = 1L;
	
	private static final String convertedStringPropertyValueFormat = "DELETE {?s <%s> ?o} INSERT {?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s <%s>(str(?o)) AS ?x ?o WHERE {?s <%s> ?o}}}";

	public DataNormalizationStringRule(Integer id, Integer groupId, Resource property) throws DataNormalizationException {
		super(id, groupId,
				property.getLocalName() + "-string-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.xstring.getLocalName(),
				
				"MODIFY",
				String.format(Locale.ROOT, convertedStringPropertyValueFormat, property.getURI(), property.getURI(), XPathFunctions.stringFunction, property.getURI()),
				"Convert " + XSD.xstring.getLocalName() + " value for the property " + property.getURI());
	}
}
