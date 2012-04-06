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
 * A manual (JDBC-template based) Role DAO implementation.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
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
		String query = "SELECT * FROM `roles`";
		return jdbcTemplate.query(query, new RoleRowMapper());
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Role load(int id) 
	{
		String query = "SELECT * FROM `roles` WHERE `id` = ?";
		Object[] args = { id };
		
		List<Role> roles = jdbcTemplate.query(query, args, new RoleRowMapper());
		
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

/**
 * 
 * Maps rows returned by JDBC-template query method to Role objects.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
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
