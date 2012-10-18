package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import java.util.Set;
import com.hp.hpl.jena.rdf.model.Resource;

public class QualityAssessmentEnumerablePropertyRule extends
		QualityAssessmentRule {
	private static final long serialVersionUID = 1L;

	public QualityAssessmentEnumerablePropertyRule(Integer groupId, Resource property, Set<Resource> possibleValues) {
		this(null, groupId, property, possibleValues);
	}

	public QualityAssessmentEnumerablePropertyRule(Integer id, Integer groupId, Resource property, Set<Resource> possibleValues) {
		super(
				id,
				groupId,
				null,
				0.8,
				null
		);
		
		StringBuilder filter = new StringBuilder();
		StringBuilder values = new StringBuilder();
		
		for (Resource value : possibleValues) {
			if (filter.length() > 0) {
				filter.append(" AND ");
				values.append(", ");
			}
			
			filter.append("?o = <" + value.getURI() + ">");
			values.append(value.getLocalName());
		}
		
		this.filter = "{?s <" + property.getURI() + "> ?o. FILTER (" + filter + ")}";
		this.description = property.getLocalName() + " can have only these values: " + values.toString();
	}
}