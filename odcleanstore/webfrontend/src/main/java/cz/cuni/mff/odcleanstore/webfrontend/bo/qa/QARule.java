package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class QARule extends BusinessObject
{
	private static final long serialVersionUID = 1L;

	private Long groupId;
	private String filter;
	private String description;
	private Double coefficient;
	
	public QARule(Long id, Long groupId, String filter, String description, Double coefficient) 
	{
		this.id = id;
		this.groupId = groupId;
		this.filter = filter;
		this.description = description;
		this.coefficient = coefficient;
	}
	
	public QARule()
	{
	}

	public Long getGroupId()
	{
		return groupId;
	}
	
	public void setGroupId(Long groupId)
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
