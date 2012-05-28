package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.dao.rowmappers.RoleRowMapper;

import java.util.List;

/**
 * The Role DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class RoleDao extends Dao<Role>
{
	@Override
	public void delete(Role item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Role item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Role item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Role> loadAll() 
	{
		return jdbcTemplate.query
		(
			"SELECT * FROM DB.ODCLEANSTORE.ROLES", 
			new RoleRowMapper()
		);
	}

	@Override
	public Role load(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
