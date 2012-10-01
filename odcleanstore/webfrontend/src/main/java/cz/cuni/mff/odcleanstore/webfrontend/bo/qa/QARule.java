package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RuleEntity;

public class QARule extends RuleEntity
{
	private static final long serialVersionUID = 1L;

	private String filter;
	private String description;
	private Double coefficient;
	
	public QARule(Integer id, Integer groupId, String filter, String description, Double coefficient) 
	{
		super(id, groupId);
		
		this.filter = filter;
		this.description = description;
		this.coefficient = coefficient;
	}
	
	public QARule()
	{
	}

	public String getFilter() 
	{
		return filter;
	}

	public String getDescription() 
	{
		return description;
	}

	public Double getCoefficient() 
	{
		return coefficient;
	}
}
