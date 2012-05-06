package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The User BO.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
public class User extends BusinessObject
{
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String email;
	private String passwordHash;
	private String salt;
	private String firstname;
	private String surname;
	
	private Set<Role> roles;
	
	/**
	 * 
	 * @param id
	 * @param username
	 * @param email
	 * @param firstname
	 * @param surname
	 */
	public User(Long id, String username, String email, String firstname, String surname)
	{
		this();
		
		this.id = id;
		this.username = username;
		this.email = email;
		this.firstname = firstname;
		this.surname = surname;
	}
	
	/**
	 * 
	 */
	public User() 
	{
		roles = new HashSet<Role>();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUsername() 
	{
		return username;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEmail() 
	{
		return email;
	}

	/**
	 * 
	 * @param passwordHash
	 */
	public void setPasswordHash(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPasswordHash()
	{
		return this.passwordHash;
	}
	
	/**
	 * 
	 * @param salt
	 */
	public void setSalt(String salt)
	{
		this.salt = salt;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSalt()
	{
		return this.salt;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFirstname() 
	{
		return firstname;
	}

	/**
	 * 
	 * @return
	 */
	public String getSurname() 
	{
		return surname;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<Role> getRoles()
	{
		return this.roles;
	}
	
	public String[] getRoleLabels()
	{
		List<String> labels = new LinkedList<String>();
		
		for (Role role : this.roles)
			labels.add(role.getLabel());
		
		return labels.toArray(new String[labels.size()]);
	}
	
	/**
	 * 
	 * @param role
	 */
	public void addRole(Role role)
	{
		this.roles.add(role);
	}
	
	/**
	 * 
	 */
	public void removeAllRoles()
	{
		this.roles.clear();
	}
	
	/**
	 * 
	 * @param role
	 * @return
	 */
	public boolean hasAssignedRole(Role role)
	{
		return this.roles.contains(role);
	}
}
