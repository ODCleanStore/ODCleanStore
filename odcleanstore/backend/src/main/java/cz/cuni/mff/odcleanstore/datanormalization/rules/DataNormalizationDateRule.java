package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.vocabulary.XPathFunctions;

/**
 * A rule used by Data Normalization Rule Generation from Ontologies
 * 
 * replaces common date substrings with correct date strings if possible
 * 
 * @author Jakub Daniel
 */
public class DataNormalizationDateRule extends DataNormalizationRule {
	private static final long serialVersionUID = 1L;
	
	private static final String convertedDatePropertyValueFormat = "DELETE {?s <%s> ?o} INSERT {?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s <%s>(str(?o)) AS ?x ?o WHERE {?s <%s> ?o}}}";

	public DataNormalizationDateRule(Integer id, Integer groupId, Resource property) throws DataNormalizationException {	
		super(id, groupId,
				property.getLocalName() + "-date-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.date.getLocalName(),
				
				"MODIFY",
				String.format(Locale.ROOT, convertedDatePropertyValueFormat, property.getURI(), property.getURI(), XPathFunctions.dateFunction, property.getURI()),
				"Convert " + XSD.date.getLocalName() + " value for the property " + property.getURI());
	}
}
