package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import com.hp.hpl.jena.rdf.model.Resource;

public class QualityAssessmentInverseFunctionalPropertyInjectivityRule extends
		QualityAssessmentRule {
	private static final long serialVersionUID = 1L;

	public QualityAssessmentInverseFunctionalPropertyInjectivityRule(Integer groupId, Resource property) {
		this(null, groupId, property);
	}

	public QualityAssessmentInverseFunctionalPropertyInjectivityRule(Integer id, Integer groupId, Resource property) {
		super(
				id,
				groupId,
				"{?s <" + property.getURI() + "> ?o} GROUP BY ?o HAVING COUNT(?s) > 1",
				0.8,
				property.getLocalName() + "-inv-functional-property-check",
				property.getLocalName() + " is InverseFunctionalProperty (value cannot be shared by two distinct subjects)"
		);
	}
}
