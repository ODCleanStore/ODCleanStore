package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Role BO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class Role extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	/** Administrator label value. */
	public static final String ADM = "ADM";
	
	/** Pipeline creator label value. */
	public static final String PIC = "PIC";
	
	/** Ontology creator label value. */
	public static final String ONC = "ONC";
	
	/** Scraper label value. */
	public static final String SCR = "SCR";
	
	/** An enumeration of standard Web Frontend roles */
	private static final List<Role> standardRoles;
	
	static 
	{
		List<Role> roles = new ArrayList<Role>();
		
		roles.add(new Role(SCR, "Scraper"));
		roles.add(new Role(ONC, "Ontology Creator"));
		roles.add(new Role(PIC, "Pipeline Creator"));
		roles.add(new Role(ADM, "Administrator"));
		
		standardRoles = Collections.unmodifiableList(roles);
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<Role> getStandardRoles() {
		return standardRoles;
	}
	
	private String label;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 */
	public Role(Integer id, String label, String description)
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
		return label.hashCode();
	}
}
