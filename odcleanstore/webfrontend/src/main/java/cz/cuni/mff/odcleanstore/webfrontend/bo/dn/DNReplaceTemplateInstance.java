package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

/**
 * The BO to represent an instance of the replace template.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNReplaceTemplateInstance extends DNTemplateInstance
{
	private static final long serialVersionUID = 1L;
	
	private Integer groupId;
	private String propertyName;
	private String pattern;
	private String replacement;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param propertyName
	 * @param pattern
	 * @param replacement
	 */
	public DNReplaceTemplateInstance(Integer id, Integer rawRuleId, Integer groupId, String propertyName, String pattern, String replacement) 
	{
		super(id, rawRuleId);
		
		this.groupId = groupId;
		this.propertyName = propertyName;
		this.pattern = pattern;
		this.replacement = replacement;
	}
	
	/**
	 * 
	 */
	public DNReplaceTemplateInstance()
	{
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
	public Integer getGroupId() 
	{
		return groupId;
	}

	/**
	 * 
	 * @return
	 */
	public String getPropertyName() 
	{
		return propertyName;
	}

	/**
	 * 
	 * @return
	 */
	public String getPattern() 
	{
		return pattern;
	}

	/**
	 * 
	 * @return
	 */
	public String getReplacement() 
	{
		return replacement;
	}
}
