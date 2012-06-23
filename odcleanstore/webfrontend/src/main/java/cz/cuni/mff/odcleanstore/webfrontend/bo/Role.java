package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.util.LinkedList;
import java.util.List;

/**
 * The Role BO.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */

public class Role extends EntityWithSurrogateKey
{
	/** an enumeration of standard frontend roles */
	public static List<Role> standardRoles;
	
	static 
	{
		standardRoles = new LinkedList<Role>();
		
		standardRoles.add(new Role("SCR", "Scraper"));
		standardRoles.add(new Role("ONC", "Ontology Creator"));
		standardRoles.add(new Role("POC", "Policy Creator"));
		standardRoles.add(new Role("ADM", "Administrator"));
	}
	
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 */
	public Role(Long id, String label, String description)
	{
		super(id);
		
		this.label = label;
		this.description = description;
	}
	
	/**
	 * 
	 * @param label
	 * @param description
	 */
	public Role(String label, String description)
	{
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

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Role))
			return false;
		
		Role otherRole = (Role) other;
		
		return label.equals(otherRole.label);
	}
	
	@Override
	public int hashCode()
	{
		return label.length();
	}
}
