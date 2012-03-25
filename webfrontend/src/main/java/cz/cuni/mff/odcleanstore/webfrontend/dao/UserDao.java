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
	 * @return
	 */
	@Override
	public List<User> loadAll() 
	{
		List<Map<String, Object>> usersRows = jdbcTemplate.queryForList(
			"SELECT * FROM `users`"
		);
		
		List<Map<String, Object>> rolesRows = jdbcTemplate.queryForList(
			"SELECT * FROM `users_roles` JOIN `roles` " +
			"ON (`roles`.`id` = `users_roles`.`role_id`)"
		);
		
		return addRolesToUsers(usersRows, rolesRows);
	}

	/**
	 * 
	 * @param item
	 */
	@Override
	public void insert(User item) 
	{
		// TODO: SQL injection alert
		// TODO: might throw DataAccessException
		jdbcTemplate.execute(
			"INSERT INTO `users` (`username`, `email`, `createdAt`) " +
			"VALUES ('" + 
				item.getUsername() + "', '" + 
				item.getEmail() + "', '" + 
				dateToMySQLTimestamp(item.getCreatedAt()) + 
			"')"
		);
	}

	/**
	 * 
	 * @param id
	 */
	@Override
	public User load(int id) 
	{
		List<Map<String, Object>> usersRows = jdbcTemplate.queryForList(
			"SELECT * FROM `users` WHERE `id` = " + id
		);
		
		
		
		List<Map<String, Object>> rolesRows = jdbcTemplate.queryForList(
			"SELECT * FROM `users_roles` JOIN `roles` " +
			"ON (`roles`.`id` = `users_roles`.`role_id`)"
		);
		
		List<User> users = addRolesToUsers(usersRows, rolesRows);

		if (!users.isEmpty()) 
			return users.get(0);
		
		return null;
	}
	
	/**
	 * Parses the given users-rows and roles-rows into a list of User instances
	 * with properly set Role instances.
	 * 
	 * @param usersRows
	 * @param rolesRows
	 * @return
	 */
	private List<User> addRolesToUsers(
		List<Map<String, Object>> usersRows, List<Map<String, Object>> rolesRows)
	{
		// construct users from row-list
		//
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
		
		// add roles to users
		//
		for (Map<String, Object> items : rolesRows)
		{
			Role role = new Role (
				(Integer) items.get("id"),
				(String) items.get("label"),
				(String) items.get("description")
			);

			User user = users.get((Integer) items.get("user_id"));
			if (user != null)
				user.addRole(role);
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
