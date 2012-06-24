package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * The Role DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class RoleDao extends DaoForEntityWithSurrogateKey<Role>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "ROLES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<Role> rowMapper;
	
	public RoleDao()
	{
		this.rowMapper = new RoleRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<Role> getRowMapper() 
	{
		return rowMapper;
	}
}
