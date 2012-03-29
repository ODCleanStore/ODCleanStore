package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

/**
 * Hibernate-based User DAO implementation.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
public class RoleDao extends Dao<Role>
{
	/**
	 * 
	 * @return
	 */
	@Override
	public List<Role> loadAll() 
	{
		return jdbcTemplate.query(
			"SELECT * FROM `roles`", 
			new RoleRowMapper()
		);
	}

	@Override
	public Role load(int id) 
	{
		List<Role> roles = jdbcTemplate.query(
			"SELECT * FROM `roles` WHERE `id` = " + id, 
			new RoleRowMapper()
		);
		
		if (roles.isEmpty())
			return null;
		
		return roles.get(0);
	}

	@Override
	public void update(Role item) 
	{
		throw new UnsupportedOperationException(
			"Cannot update roles in the database."
		);
	}

	@Override
	public void insert(Role item) 
	{
		throw new UnsupportedOperationException(
			"Cannot insert new roles into the database"
		);
	}

}

class RoleRowMapper implements ParameterizedRowMapper
{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Role(
			rs.getInt("id"),
			rs.getString("label"),
			rs.getString("description")
		);
	}
}
