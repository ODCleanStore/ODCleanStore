package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

/**
 * A rule used by Data Normalization Rule Generation from Ontologies
 * 
 * convert to non negative integral value by calculating number(x), default to 0 if null or incompatible or > 0
 * 
 * Unwanted behaviour of different approaches:
 * 
 * floor(-1.2) = -2
 * 
 * @author Jakub Daniel
 */
public class DataNormalizationNonPositiveIntegerRule extends DataNormalizationRule {
	private static final long serialVersionUID = 1L;
	
	private static final String convertedNonPositiveIntegerPropertyValueFormat = "DELETE {?s <%s> ?o} INSERT {?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s ceiling(bif:__min_notnull(fn:number(str(?o)), 0)) AS ?x ?o WHERE {?s <%s> ?o}}}";

	public DataNormalizationNonPositiveIntegerRule(Integer id, Integer groupId, Resource property) throws DataNormalizationException {
		super(id, groupId,
				property.getLocalName() + "-non-positive-integer-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.nonPositiveInteger.getLocalName(),
				
				"MODIFY",
				String.format(Locale.ROOT, convertedNonPositiveIntegerPropertyValueFormat, property.getURI(), property.getURI(), property.getURI()),
				"Convert " + XSD.nonPositiveInteger.getLocalName() + " value for the property " + property.getURI());
	}
}