package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO which represents an OI output type.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIOutputType extends EntityWithSurrogateKey 
{
	public static final String DB_OUTPUT_LABEL = "DB";
	public static final String FILE_OUTPUT_LABEL = "FILE";
	
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 */
	public OIOutputType(Integer id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
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
}
