package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.vocabulary.XPathFunctions;

public class DataNormalizationDateRule extends DataNormalizationRule {
	private static final long serialVersionUID = 1L;
	
	private static final String insertConvertedDatePropertyValueFormat = "{?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s <%s>(str(?o)) AS ?x WHERE {?s <%s> ?o}}}";
	private static final String deleteUnconvertedDatePropertyValueFormat = "{?s <%s> ?o} WHERE {GRAPH $$graph$$ {?s <%s> ?o. FILTER (?o != <%s>(str(?o)))}}";

	public DataNormalizationDateRule(Integer id, Integer groupId, Resource property) throws DataNormalizationException {	
		super(id, groupId,
				property.getLocalName() + "-date-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.date.getLocalName(),
				
				"INSERT",
				String.format(Locale.ROOT, insertConvertedDatePropertyValueFormat, property.getURI(), XPathFunctions.dateFunction, property.getURI()),
				"Create proper " + XSD.date.getLocalName() + " value for the property " + property.getURI(),

				"DELETE",
				String.format(Locale.ROOT, deleteUnconvertedDatePropertyValueFormat, property.getURI(), property.getURI(), XPathFunctions.dateFunction),
				"Remove all improper values of the property " + property.getURI());
	}
}
