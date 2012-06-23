package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class OIRulesGroup extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private List<OIRule> rules;
	
	public OIRulesGroup(Long id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
		
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
	
	public void setRules(List<OIRule> rules)
	{
		this.rules = rules;
	}
	
	public List<OIRule> getRules()
	{
		return rules;
	}
}
