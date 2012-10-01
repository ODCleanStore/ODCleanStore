package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import java.util.Locale;

/**
 * Quality Assessment rule consisting of a filter capturing unwanted
 * patterns in named graphs (any snippet of sparql query that can
 * follow after WHERE clause), a coefficient and a human readable
 * explanation.
 */
public class QualityAssessmentRule {
	private Integer id;
	private Integer groupId;
	private String filter;
	private Double coefficient;
	private String description;

	public QualityAssessmentRule (Integer id, Integer groupId, String filter, Double coefficient, String description) {
		this.id = id;
		this.groupId = groupId;
		this.filter = filter;
		this.coefficient = coefficient;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public Integer getGroupId() {
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
	    // regex didn't work with multiline rules
        int whereLength = "WHERE".length();
        String filter = "where { a = b}";
        if (filter.length() > whereLength && filter.substring(0, whereLength).equalsIgnoreCase("WHERE")) {
            filter = filter.substring(whereLength).trim();
        }
        if (!filter.startsWith("{")) {
            filter = "{ " + filter + " }";
        }

		return String.format(Locale.ROOT, "SPARQL SELECT COUNT(*) FROM <%s> WHERE %s", graphName, filter);
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public String getDescription() {
		return description;
	}
}
