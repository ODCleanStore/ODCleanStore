package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;;

public class QARulesGroup extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private List<QARule> rules;
	
	public QARulesGroup(Long id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
		
		this.rules = new LinkedList<QARule>();
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
	
	public void setRules(List<QARule> rules)
	{
		this.rules = rules;
	}
	
	public List<QARule> getRules()
	{
		return rules;
	}
}
