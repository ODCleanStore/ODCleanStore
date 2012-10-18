package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import com.hp.hpl.jena.rdf.model.Resource;

public class QualityAssessmentFunctionalPropertyAmbiguityRule extends
		QualityAssessmentRule {
	private static final long serialVersionUID = 1L;
	
	public QualityAssessmentFunctionalPropertyAmbiguityRule(Integer groupId, Resource property) {
		this(null, groupId, property);
	}

	public QualityAssessmentFunctionalPropertyAmbiguityRule(Integer id, Integer groupId, Resource property) {
		super(
				id,
				groupId,
				"{?s <" + property.getURI() + "> ?o} GROUP BY ?s HAVING COUNT(?o) > 1",
				0.8,
				property.getLocalName() + " is FunctionalProperty (can have only 1 unique value)"
		);
	}
}
