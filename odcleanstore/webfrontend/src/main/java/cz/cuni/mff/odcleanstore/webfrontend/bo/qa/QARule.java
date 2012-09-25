package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class QARule extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
	private String filter;
	private String description;
	private Double coefficient;
	
	public QARule(Integer id, Integer groupId, String filter, String description, Double coefficient) 
	{
		super(id);
		
		this.groupId = groupId;
		this.filter = filter;
		this.description = description;
		this.coefficient = coefficient;
	}
	
	public QARule()
	{
	}

	public Integer getGroupId()
	{
		return groupId;
	}
	
	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
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
