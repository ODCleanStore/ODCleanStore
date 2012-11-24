package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

public class DataNormalizationIntegerRule extends DataNormalizationRule {
	private static final long serialVersionUID = 1L;
	
	private static final String convertedIntegerPropertyValueFormat = "DELETE {?s <%s> ?o} INSERT {?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s (bif:sign(fn:number(str(?o))) * floor(bif:__max_notnull(abs(fn:number(str(?o))), 0))) AS ?x ?o WHERE {?s <%s> ?o}}}";

	public DataNormalizationIntegerRule(Integer id, Integer groupId, Resource property) throws DataNormalizationException {
		super(id, groupId,
				property.getLocalName() + "-integer-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.integer.getLocalName(),
				
				"MODIFY",
				String.format(Locale.ROOT, convertedIntegerPropertyValueFormat, property.getURI(), property.getURI(), property.getURI()),
				"Convert " + XSD.integer.getLocalName() + " value for the property " + property.getURI());
	}
}