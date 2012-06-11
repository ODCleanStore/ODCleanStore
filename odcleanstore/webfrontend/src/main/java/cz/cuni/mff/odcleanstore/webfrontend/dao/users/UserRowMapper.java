package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements ParameterizedRowMapper<User>
{
	public User mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		User user = new User(
			rs.getLong("id"),
			rs.getString("username"),
			rs.getString("email"),
			rs.getString("firstname"),
			rs.getString("surname")
		);
		
		user.setPasswordHash(rs.getString("passwordHash"));
		user.setSalt(rs.getString("salt"));
		
		return user;
	}
}
