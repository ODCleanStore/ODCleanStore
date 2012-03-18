package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

/**
 * Hibernate-based User DAO implementation.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
public class UserDaoImpl extends DaoImpl<User>
{
	/**
	 * 
	 */
	public UserDaoImpl() 
	{
		super(User.class);
	}
}
