package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

public class OIRulesGroup extends RulesGroupEntity implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private List<OIRule> rules;
	private Integer authorId;
	
	public OIRulesGroup(Integer id, String label, String description, Integer authorId) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
		this.authorId = authorId;
		
		this.rules = new LinkedList<OIRule>();
	}

	public OIRulesGroup() 
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
	
	public void setRules(List<OIRule> rules)
	{
		this.rules = rules;
	}
	
	public List<OIRule> getRules()
	{
		return rules;
	}
}
