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
		super(id, groupId, rawRuleId);
		
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
