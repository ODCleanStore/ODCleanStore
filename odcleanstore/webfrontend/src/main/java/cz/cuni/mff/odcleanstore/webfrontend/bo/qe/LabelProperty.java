package cz.cuni.mff.odcleanstore.webfrontend.bo.qe;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Dusan
 *
 */
public class LabelProperty extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private String property;

	/**
	 * 
	 * @param id
	 * @param property
	 */
	public LabelProperty(Integer id, String property) 
	{
		super(id);
		this.property = property;
	}
	
	/**
	 * 
	 * @param property
	 */
	public LabelProperty(String property)
	{
		this.property = property;
	}
	
	/**
	 * 
	 */
	public LabelProperty()
	{
		super();
	}
	
	public String getProperty()
	{
		return property;
	}
}
