package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The User BO. Serves directly as a Hibernate entity.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
@Entity
@Table(name = "users", catalog = "odcleanstore")
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Long id;
	private String username;
	private String email;
	private Date createdAt;

	/**
	 * 
	 * @return
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId()
	{
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * 
	 * @return
	 */
	@Column(name = "username", unique = true, nullable = false, length = 255)
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
	@Column(name = "email", unique = true, nullable = false, length = 255)
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
	@Column(name = "createdAt", nullable = false)
	public Date getCreatedAt() 
	{
		return createdAt;
	}

	/**
	 * 
	 * @param createdAt
	 */
	public void setCreatedAt(Date createdAt) 
	{
		this.createdAt = createdAt;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public String toString()
	{
		return "USER: [" + id + "; " + username + "; " + email + "];";
	}
}
