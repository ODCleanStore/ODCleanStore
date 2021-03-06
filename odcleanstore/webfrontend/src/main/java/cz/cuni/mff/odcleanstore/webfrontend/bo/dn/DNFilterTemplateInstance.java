package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

/**
 * The BO to represent an instance of the filter template.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNFilterTemplateInstance extends DNTemplateInstance
{
	private static final long serialVersionUID = 1L;

	private String propertyName;
	private String pattern;
	private Boolean keep;
	
	/**
	 * 
	 * @param id
	 * @param rawRuleId
	 * @param groupId
	 * @param propertyName
	 * @param pattern
	 * @param keep
	 */
	public DNFilterTemplateInstance(Integer id, Integer rawRuleId, Integer groupId, String propertyName, String pattern, Boolean keep) 
	{
		super(id, groupId, rawRuleId);
		
		this.propertyName = propertyName;
		this.pattern = pattern;
		this.keep = keep;
	}
	
	/**
	 * 
	 */
	public DNFilterTemplateInstance()
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
	public Boolean getKeep() 
	{
		return keep;
	}
}
