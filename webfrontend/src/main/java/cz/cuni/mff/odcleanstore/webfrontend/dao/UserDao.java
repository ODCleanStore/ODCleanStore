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
public class UserDao extends Dao<User>
{
	/**
	 * 
	 */
	public List<User> loadAll() 
	{
		List<Map<String, Object>> usersRows = jdbcTemplate.queryForList(
			"SELECT * FROM `users`"
		);
		
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		for (Map<String, Object> row : usersRows)
		{
			User user = new User(
				(Integer) row.get("id"),
				(String) row.get("username"),
				(String) row.get("email"),
				(Date) row.get("createdAt")
			);
			
			users.put(user.getId(), user);
		}
		
		List<Map<String, Object>> rolesRows = jdbcTemplate.queryForList(
			"SELECT * FROM `users_roles` JOIN `roles` " +
			"ON (`roles`.`id` = `users_roles`.`role_id`)"
		);
		
		for (Map<String, Object> items : rolesRows)
		{
			Role role = new Role (
				(Integer) items.get("id"),
				(String) items.get("label"),
				(String) items.get("description")
			);
			
			users.get((Integer) items.get("user_id")).addRole(role);
		}
		
		return new LinkedList<User>(users.values());
	}
}

/*
class UserRowMapper implements ParameterizedRowMapper
{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new User(
			rs.getLong("id"),
			rs.getString("username"),
			rs.getString("email"),
			rs.getDate("createdAt")
		);
	}
}
*/
