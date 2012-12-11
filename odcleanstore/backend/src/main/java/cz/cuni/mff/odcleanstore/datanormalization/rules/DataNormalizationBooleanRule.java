package cz.cuni.mff.odcleanstore.datanormalization.rules;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import java.util.Locale;

/**
 * A rule used by Data Normalization Rule Generation from Ontologies
 *
 * replaces common expressions used to denote logical true / false: 0, 1, t, f, true, false, y, n, yes, no
 *
 * @author Jakub Daniel
 */
public class DataNormalizationBooleanRule extends DataNormalizationRule {
	private static final long serialVersionUID = 1L;

	private static final String boolTruePattern = "?o = '1' OR bif:lcase(str(?o)) = 'true' OR bif:lcase(str(?o)) = 'yes' OR bif:lcase(str(?o)) = 't' OR bif:lcase(str(?o)) = 'y'";
	private static final String boolFalsePattern = "?o = '0' OR bif:lcase(str(?o)) = 'false' OR bif:lcase(str(?o)) = 'no' OR bif:lcase(str(?o)) = 'f' OR bif:lcase(str(?o)) = 'n'";

	private static final String convertedTruePropertyValueFormat = "DELETE {?s <%s> ?o} INSERT {?s <%s> ?t} WHERE {GRAPH $$graph$$ {SELECT ?s (if(" + boolTruePattern + ", 1, 0)) AS ?t ?o WHERE {?s <%s> ?o. FILTER (" + boolTruePattern + " OR " + boolFalsePattern + ")}}}";

	public DataNormalizationBooleanRule (Integer id, Integer groupId, Resource property) throws DataNormalizationException {
		super(id, groupId,
				property.getLocalName() + "-bool-conversion",
				"Convert " + property.getLocalName() + " into " + XSD.xboolean.getLocalName(),

				"MODIFY",
				String.format(Locale.ROOT, convertedTruePropertyValueFormat, property.getURI(), property.getURI(), property.getURI()),
				"Convert " + XSD.xboolean.getLocalName() + " value for the property " + property.getURI() + " (\"1\", \"true\", ...)");
	}
}
