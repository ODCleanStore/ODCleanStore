package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.util.Calendar;
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
		this();
		
		this.id = id;
		this.username = username;
		this.email = email;
		this.createdAt = date;
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
	 * @param username
	 */
	public void setUsername(String username)
	{
		this.username = username;
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
	 * @param email
	 */
	public void setEmail(String email)
	{
		this.email = email;
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
	 * @param createAt
	 */
	public void setCreatedAt(Date createdAt)
	{
		this.createdAt = createdAt;
	}
	
	/**
	 * 
	 */
	public void setCreatedAtToNow()
	{
		Calendar calendar = Calendar.getInstance();
		this.createdAt = calendar.getTime();
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
