package cz.cuni.mff.odcleanstore.datanormalization.rules;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import java.util.Locale;

/**
 * A rule used by Data Normalization Rule Generation from Ontologies
 *
 * convert to non negative integral value by calculating number(x), default to 0 if null or incompatible or < 0
 *
 * @author Jakub Daniel
 */
public class DataNormalizationNonNegativeIntegerRule extends DataNormalizationRule {
	private static final long serialVersionUID = 1L;

	private static final String convertedNonNegativeIntegerPropertyValueFormat = "DELETE {?s <%s> ?o} INSERT {?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s bif:floor(bif:__max_notnull(fn:number(str(?o)), 0)) AS ?x ?o WHERE {?s <%s> ?o}}}";

	public DataNormalizationNonNegativeIntegerRule(Integer id, Integer groupId, Resource property) throws DataNormalizationException {
		super(id, groupId,
				property.getLocalName() + "-non-negative-integer-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.nonNegativeInteger.getLocalName(),

				"MODIFY",
				String.format(Locale.ROOT, convertedNonNegativeIntegerPropertyValueFormat, property.getURI(), property.getURI(), property.getURI()),
				"Convert " + XSD.nonNegativeInteger.getLocalName() + " value for the property " + property.getURI());
	}
}
