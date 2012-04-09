package cz.cuni.mff.odcleanstore.qualityassessment.rules;

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
	
	public String toString() {
		return "SELECT * WHERE " + this.filter;
	}
	
	public Float getCoefficient() {
		return coefficient;
	}
	
	public String getComment() {
		return comment;
	}
}
