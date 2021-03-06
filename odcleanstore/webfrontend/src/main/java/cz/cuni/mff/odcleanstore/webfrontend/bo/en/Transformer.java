package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent a transformer.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class Transformer extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private String jarPath;
	private String workDirPath;
	private String fullClassName;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param jarPath
	 * @param workDirPath
	 * @param fullClassName
	 */
	public Transformer(Integer id, String label, String description, String jarPath, String workDirPath,
		String fullClassName) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
		this.jarPath = jarPath;
		this.workDirPath = workDirPath;
		this.fullClassName = fullClassName;
	}
	
	/**
	 * 
	 */
	public Transformer()
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
	public String getDescription() 
	{
		return description;
	}

	/**
	 * 
	 * @return
	 */
	public String getJarPath() 
	{
		return jarPath;
	}

	/**
	 * 
	 * @return
	 */
	public String getWorkDirPath()
	{
		return workDirPath;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFullClassName() 
	{
		return fullClassName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSimpleClassName() 
	{
		return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
	}
}
