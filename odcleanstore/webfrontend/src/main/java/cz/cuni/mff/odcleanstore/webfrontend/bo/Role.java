package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.io.Serializable;

/**
 * The Role BO.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */

public class Role implements Serializable
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
	public Integer getId()
	{
		return id;
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
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Role))
			return false;
		
		Role otherRole = (Role) other;
		
		return
			(this.id == otherRole.id) &&
			this.name.equals(otherRole.name) &&
			this.description.equals(otherRole.description);
	}
}
