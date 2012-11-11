package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
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
	private String label;
	private String description;
	private Double coefficient;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param filter
	 * @param label
	 * @param description
	 * @param coefficient
	 */
	public QARule(Integer id, Integer groupId, String filter, String label, String description, Double coefficient) 
	{
		super(id, groupId);
		
		this.filter = filter;
		this.label = label;
		this.description = description;
		this.coefficient = coefficient;
	}
	
	/**
	 * 
	 * @param rule
	 */
	public QARule(QualityAssessmentRule rule)
	{
		super(rule.getId(), rule.getGroupId());
		
		this.filter = rule.getFilter();
		this.coefficient = rule.getCoefficient();
		this.label = rule.getLabel();
		this.description = rule.getDescription();
	}
	
	/**
	 * 
	 */
	public QARule()
	{
	}

	/**
	 * 
	 * @return
	 */
	public String getFilter() 
	{
		return filter;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() 
	{
		return label;
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
