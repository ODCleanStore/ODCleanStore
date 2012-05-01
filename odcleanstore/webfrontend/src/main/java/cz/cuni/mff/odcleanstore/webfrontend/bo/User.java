package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.util.HashSet;
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
