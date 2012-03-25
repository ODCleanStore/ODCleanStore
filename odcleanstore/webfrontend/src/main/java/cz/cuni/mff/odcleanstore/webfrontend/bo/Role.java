package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * The Role BO.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */

public class Role
{
	public enum NAME { SCR, ONC, POC, ADM };
	
	private Integer id;
	private String name;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param description
	 */
	public Role(Integer id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() 
	{
		return name;
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
