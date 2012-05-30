package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.rowmappers.RoleRowMapper;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * The Role DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class RoleDao extends Dao<Role>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "ROLES";
	
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
	
	@Override
	public List<Role> loadAll() 
	{
		return loadAllRaw();
	}

	@Override
	public Role load(Long id) 
	{
		return loadRaw(id);
	}
}
