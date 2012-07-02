package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Dusan
 *
 */
public class OIRule extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private Long groupId;
	private String label;
	private String linkType;
	private String sourceRestriction;
	private String targetRestriction;
	private String linkageRule;
	private Double filterThreshold;
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
	public OIRule(Long id, Long groupId, String label, String linkType, String sourceRestriction, 
		String targetRestriction, String linkageRule, Double filterThreshold, Integer filterLimit) 
	{
		super(id);
		
		this.groupId = groupId;
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
	public Long getGroupId() 
	{
		return groupId;
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
	public Double getFilterThreshold() 
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

	/**
	 * 
	 * @param groupId
	 */
	public void setGroupId(Long groupId) 
	{
		this.groupId = groupId;
	}
}
