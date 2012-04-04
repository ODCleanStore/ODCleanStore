package cz.cuni.mff.odcleanstore.qualityassessment;

/**
 * A rule to be applied
 */
public class Rule
{
	private Integer id;
	private String filter;
	
	public Float coeficient;
	public String comment;
	
	/**
	 * @param filter A snippet of SPARQL condition which will be used to fetch graphs that
	 * satisfy the rules semantic condition.
	 * @param coeficient A value that modifies the graphs score
	 * @param comment A human readable comment on what is going on when this rule applies
	 */
	public Rule (Integer id, String filter, Float coeficient, String comment)
	{
		this.id = id;
		this.filter = filter;
		this.coeficient = coeficient;
		this.comment = comment;
	}
	
	/**
	 * @return Will return a valid SPARQL to be performed over the database
	 */
	public String compile ()
	{
		return "SELECT * WHERE " + filter;
	}
	
	public Integer getId ()
	{
		return id;
	}
}


