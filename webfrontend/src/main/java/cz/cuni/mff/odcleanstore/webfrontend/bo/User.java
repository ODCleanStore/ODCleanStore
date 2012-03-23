package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * The User BO.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
public class User
{
	private Integer id;
	
	private String username;
	private String email;
	private Date createdAt;
	
	private Set<Role> roles;

	/**
	 * 
	 * @param id
	 * @param username
	 * @param email
	 * @param date
	 */
	public User(Integer id, String username, String email, Date date)
	{
		this.id = id;
		this.username = username;
		this.email = email;
		this.createdAt = date;
		
		roles = new HashSet<Role>(0);
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
	public Date getCreatedAt() 
	{
		return createdAt;
	}

	/**
	 * 
	 * @return
	 */
	public Set<Role> getRoles()
	{
		return roles;
	}
	
	/**
	 * 
	 * @param role
	 */
	public void addRole(Role role)
	{
		roles.add(role);
	}
	
	/**
	 * 
	 * @param role
	 */
	public void removeRole(Role role)
	{
		roles.remove(role);
	}
}
