package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

/**
 * The BO to represent an instance of the concatenate template.
 * 
 * @author Jakub Daniel
 *
 */
public class DNConcatenateTemplateInstance extends DNTemplateInstance
{
	private static final long serialVersionUID = 1L;

	private String propertyName;
	private String delimiter;
	
	/**
	 * 
	 * @param id
	 * @param rawRuleId
	 * @param groupId
	 * @param propertyName
	 * @param delimiter
	 */
	public DNConcatenateTemplateInstance(Integer id, Integer rawRuleId, Integer groupId, String propertyName, String delimiter) 
	{
		super(id, groupId, rawRuleId);
		
		this.propertyName = propertyName;
		this.delimiter = delimiter;
	}
	
	/**
	 * 
	 */
	public DNConcatenateTemplateInstance()
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
	public String getDelimiter()
	{
		return delimiter;
	}
}