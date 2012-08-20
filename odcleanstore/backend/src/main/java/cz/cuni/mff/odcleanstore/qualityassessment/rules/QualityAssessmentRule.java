package cz.cuni.mff.odcleanstore.qualityassessment.rules;

/**
 * Quality Assessment rule consisting of a filter capturing unwanted
 * patterns in named graphs (any snippet of sparql query that can
 * follow after WHERE clause), a coefficient and a human readable
 * explanation.
 */
public class QualityAssessmentRule {
	private Long id;
	private Long groupId;
	private String filter;
	private Double coefficient;
	private String description;

	public QualityAssessmentRule (Long id, Long groupId, String filter, Double coefficient, String description) {
		this.id = id;
		this.groupId = groupId;
		this.filter = filter;
		this.coefficient = coefficient;
		this.description = description;
	}

	public Long getId() {
		return id;
	}
	
	public Long getGroupId() {
		return groupId;
	}
	public String getFilter() {
		return filter;
	}

	/**
	 * Constructs a SPARQL query for a particular graph.
	 * @param graphName the name of the graph to apply the rule to
	 * @return the SPARQL
	 */
	public String toString(String graphName) {
		return String.format("SPARQL SELECT COUNT(*) FROM <%s> WHERE %s", graphName, filter);
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public String getDescription() {
		return description;
	}
}