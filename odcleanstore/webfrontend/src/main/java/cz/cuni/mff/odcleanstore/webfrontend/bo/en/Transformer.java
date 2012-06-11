package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class Transformer extends BusinessObject
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private String jarPath;
	private String fullClassName;
	
	/**
	 * 
	 * @param ig
	 * @param label
	 * @param description
	 * @param jarPath
	 * @param fullClassName
	 */
	public Transformer(Long id, String label, String description, String jarPath,
		String fullClassName) 
	{
		this.id = id;
		this.label = label;
		this.description = description;
		this.jarPath = jarPath;
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
	public String getFullClassName() 
	{
		return fullClassName;
	}
}