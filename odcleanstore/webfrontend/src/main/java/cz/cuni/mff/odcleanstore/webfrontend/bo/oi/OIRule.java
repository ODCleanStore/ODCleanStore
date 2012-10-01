package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import java.math.BigDecimal;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RuleEntity;

/**
 * 
 * @author Dusan
 *
 */
public class OIRule extends RuleEntity
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String linkType;
	private String sourceRestriction;
	private String targetRestriction;
	private String linkageRule;
	private BigDecimal filterThreshold;
	private Integer filterLimit;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param label
	 * @param linkType
	 * @param sourceRestriction
	 * @param targetRestriction
	 * @param linkageRule
	 * @param filterThreshold
	 * @param filterLimit
	 */
	public OIRule(Integer id, Integer groupId, String label, String linkType, String sourceRestriction, 
		String targetRestriction, String linkageRule, BigDecimal filterThreshold, Integer filterLimit) 
	{
		super(id, groupId);
		
		this.label = label;
		this.linkType = linkType;
		this.sourceRestriction = sourceRestriction;
		this.targetRestriction = targetRestriction;
		this.linkageRule = linkageRule;
		this.filterThreshold = filterThreshold;
		this.filterLimit = filterLimit;
	}

	/**
	 * 
	 */
	public OIRule()
	{
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
	public String getLinkType() 
	{
		return linkType;
	}
	
	/**
	 * 
	 * @return
	 */
	public void setLinkType(String linkType) 
	{
		this.linkType = linkType;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getSourceRestriction() 
	{
		return sourceRestriction;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTargetRestriction() 
	{
		return targetRestriction;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLinkageRule() 
	{
		return linkageRule;
	}
	
	/**
	 * 
	 * @return
	 */
	public BigDecimal getFilterThreshold() 
	{
		return filterThreshold;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getFilterLimit() 
	{
		return filterLimit;
	}

}
