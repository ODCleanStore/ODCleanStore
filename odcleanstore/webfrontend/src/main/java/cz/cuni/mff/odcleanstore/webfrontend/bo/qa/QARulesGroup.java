package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

public class QARulesGroup extends RulesGroupEntity 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private Integer authorId;
	
	public QARulesGroup(Integer id, String label, String description, Integer authorId) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
		this.authorId = authorId;
	}

	public QARulesGroup() 
	{
	}

	public String getLabel() 
	{
		return label;
	}

	public String getDescription() 
	{
		return description;
	}
	
	public Integer getAuthorId() 
	{
		return authorId;
	}
	
	/**
	 * 
	 * @param authorId
	 */
	public void setAuthorId(Integer authorId) 
	{
		this.authorId = authorId;
	}
}
