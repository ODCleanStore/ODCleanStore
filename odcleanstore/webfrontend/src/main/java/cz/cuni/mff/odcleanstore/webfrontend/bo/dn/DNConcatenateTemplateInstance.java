package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

/**
 * The BO to represent an instance of the concat template.
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
	 * @param groupId
	 * @param propertyName
	 * @param pattern
	 * @param keep
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
	
	public String getDelimiter()
	{
		return delimiter;
	}
}