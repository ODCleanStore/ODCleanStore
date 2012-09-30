package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO which represents a QA rule.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class QARule extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Integer groupId;
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
		super(id);
		
		this.groupId = groupId;
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

	/**
	 * 
	 * @return
	 */
	public Integer getGroupId()
	{
		return groupId;
	}
	
	/**
	 * 
	 * @param groupId
	 */
	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
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
