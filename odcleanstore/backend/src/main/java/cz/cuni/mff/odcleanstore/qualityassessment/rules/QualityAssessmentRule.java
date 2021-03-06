package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import java.io.Serializable;
import java.util.Locale;

/**
 * Quality Assessment rule consisting of a filter capturing unwanted
 * patterns in named graphs (any snippet of sparql query that can
 * follow after WHERE clause), a coefficient and a human readable
 * explanation.
 */
public class QualityAssessmentRule implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Integer id;
	protected Integer groupId;
	protected String filter;
	protected Double coefficient;
	protected String label;
	protected String description;

	public QualityAssessmentRule (Integer id, Integer groupId, String filter, Double coefficient, String label, String description) {
		this.id = id;
		this.groupId = groupId;
		this.filter = filter;
		this.coefficient = coefficient;
		this.label = label;
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

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
