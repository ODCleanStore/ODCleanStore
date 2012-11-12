package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.vocabulary.XPathFunctions;

public class DataNormalizationStringRule extends DataNormalizationRule
{
	private static final long serialVersionUID = 1L;
	
	private static final String insertConvertedStringPropertyValueFormat = "{?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s <%s>(str(?o)) AS ?x WHERE {?s <%s> ?o}}}";
	private static final String deleteUnconvertedStringPropertyValueFormat = "{?s <%s> ?o} WHERE {GRAPH $$graph$$ {?s <%s> ?o. FILTER (?o != <%s>(str(?o)))}}";

	public DataNormalizationStringRule(Integer id, Integer groupId, Resource property) throws DataNormalizationException {
		super(id, groupId,
				property.getLocalName() + "-string-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.xstring.getLocalName(),
				
				"INSERT",
				String.format(Locale.ROOT, insertConvertedStringPropertyValueFormat, property.getURI(), XPathFunctions.stringFunction, property.getURI()),
				"Create proper " + XSD.xstring.getLocalName() + " value for the property " + property.getURI(),

				"DELETE",
				String.format(Locale.ROOT, deleteUnconvertedStringPropertyValueFormat, property.getURI(), property.getURI(), XPathFunctions.stringFunction),
				"Remove all improper values of the property " + property.getURI());
	}
}
