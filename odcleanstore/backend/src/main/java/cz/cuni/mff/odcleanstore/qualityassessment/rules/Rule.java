package cz.cuni.mff.odcleanstore.qualityassessment.rules;

/**
 * Quality Assessment rule consisting of a filter capturing unwanted
 * patterns in named graphs (any snippet of sparql query that can
 * follow after WHERE clause), a coefficient and a human readable
 * explanation.
 */
public class Rule {
	private Integer id;
	private String filter;
	private Float coefficient;
	private String comment;

	public Rule (Integer id, String filter, Float coefficient, String comment) {
		this.id = id;
		this.filter = filter;
		this.coefficient = coefficient;
		this.comment = comment;
	}

	public Integer getId() {
		return id;
	}

	/**
	 * Constructs a SPARQL query for a particular graph.
	 */
	public String toString(String graphName) {
		return String.format("SPARQL SELECT COUNT(*) FROM <%s> WHERE %s", graphName, this.filter);
	}

	public Float getCoefficient() {
		return coefficient;
	}

	public String getComment() {
		return comment;
	}
}
