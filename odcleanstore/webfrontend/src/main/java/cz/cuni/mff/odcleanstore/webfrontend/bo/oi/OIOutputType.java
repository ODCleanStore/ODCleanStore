package cz.cuni.mff.odcleanstore.webfrontend.bo.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class OIOutputType extends EntityWithSurrogateKey 
{
	public static final String DB_OUTPUT_LABEL = "DB";
	public static final String FILE_OUTPUT_LABEL = "FILE";
	
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	public OIOutputType(Long id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
	}

	public String getLabel() 
	{
		return label;
	}

	public String getDescription() 
	{
		return description;
	}
}
