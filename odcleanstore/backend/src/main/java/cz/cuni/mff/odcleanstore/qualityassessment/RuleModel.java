package cz.cuni.mff.odcleanstore.qualityassessment;

import java.sql.Connection;

public class RuleModel
{
	public interface Callback
	{
		public void perform (Object o);
	}
	
	private Connection connection;
	private Callback update;
	
	public RuleModel(Connection connection, Callback update)
	{
		this.connection = connection;
		this.update = update;
	}
	
	public Rule getRule(Integer id)
	{
		//...
		return new Rule(id, "", 0.0f, "");
	}
  
	/**
	 * How to provide similar but cleaner interface for Engine to define an Action to be performed when a rule updates
	 * Or should it be completely QA internal behavior
	 * @param rule
	 * @param callback
	 */
	public void updateRule(Rule rule)
	{
		//...
		update.perform(rule);
	}
	
	public void removeRule(Integer id)
	{
		//...
		update.perform(rule);
	}
	
	public void removeRule(Rule rule)
	{
		removeRule(rule.getId());
	}
	
	public RuleSet fetchRules()
	{
		return new RuleSet();
	}
}