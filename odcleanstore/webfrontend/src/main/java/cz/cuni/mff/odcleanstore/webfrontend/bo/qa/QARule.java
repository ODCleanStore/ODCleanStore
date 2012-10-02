package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RuleEntity;

/**
 * The BO which represents a QA rule.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class QARule extends RuleEntity
{
	private static final long serialVersionUID = 1L;

	private String filter;
	private String description;
	private Double coefficient;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param filter
	 * @param description
	 * @param coefficient
	 */
	public QARule(Integer id, Integer groupId, String filter, String description, Double coefficient) 
	{
		super(id, groupId);
		
		this.filter = filter;
		this.description = description;
		this.coefficient = coefficient;
	}
	
	/**
	 * 
	 */
	public QARule()
	{
	}

	public String getFilter() 
	{
		return filter;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() 
	{
		return description;
	}

	/**
	 * 
	 * @return
	 */
	public Double getCoefficient() 
	{
		return coefficient;
	}
}
