package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * The User DAO.
 * 
 * TODO: Implement Exceptions handling.
 * TODO: Implement transactions.
 * 
 * TODO: Consider the idea that a Dao can lookup another dao and query it for raw objects.
 * TODO: Also consider creating a Dao for the ROLES_TO_USERS_ASSIGNMENT table.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class UserDao extends DaoForEntityWithSurrogateKey<User>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "USERS";
	public static final String PERMISSIONS_TABLE_NAME = TABLE_NAME_PREFIX + "ROLES_ASSIGNED_TO_USERS";
	
	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<User> rowMapper;
	
	public UserDao()
	{
		this.rowMapper = new UserRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}


	@Override
	protected ParameterizedRowMapper<User> getRowMapper() 
	{
		return rowMapper;
	}
	
	/*
	 	=======================================================================
	 	LOAD SINGLE ROW
	 	=======================================================================
	*/
	
	@Override
	public User load(Long id) 
	{	
		User user = loadRaw(id);
		user.setRoles(loadRolesForUser(id));
		return user;
	}
	
	@Override
	public User loadBy(String columnName, Object value)
	{
		User user = loadRawBy(columnName, value);
		user.setRoles(loadRolesForUser(user.getId()));
		return user;
	}
	
	private Set<Role> loadRolesForUser(Long userId)
	{
		String query = 
			"SELECT * FROM " + PERMISSIONS_TABLE_NAME + " AS P " +
			"JOIN " + RoleDao.TABLE_NAME + " AS R ON R.id = P.roleId " +
			"WHERE userId = ?";
		
		Object[] params = { userId };
		
		List<Role> rolesList = getJdbcTemplate().query(query, params, new RoleRowMapper());
		return new HashSet<Role>(rolesList);
	}
	
	/*
	 	=======================================================================
	 	LOAD ALL ROWS
	 	=======================================================================
	*/
	
	@Override
	public List<User> loadAll() 
	{
		Map<Long, User> usersMapping = convertListToHashMap(loadAllRaw());
		Map<Long, Role> rolesMapping = convertListToHashMap(loadAllRolesRaw());

		List<Pair<Long, Long>> assignedRoles = loadAllPermissionRecordsRaw();
		
		// assign rules to users according to the assignment
		//
		for (Pair<Long, Long> assignment : assignedRoles)
		{
			User targetUser = usersMapping.get(assignment.getFirst());
			Role targetRole = rolesMapping.get(assignment.getSecond());
			
			targetUser.addRole(targetRole);
		}
		
		return new LinkedList<User>(usersMapping.values());
	}	
	
	private <T extends EntityWithSurrogateKey> Map<Long, T> convertListToHashMap(List<T> list)
	{
		Map<Long, T> mapping = new HashMap<Long, T>();
		
		for (T item : list)
			mapping.put(item.getId(), item);
		
		return mapping;
	}
	
	private List<Role> loadAllRolesRaw()
	{
		String query = "SELECT * FROM " + RoleDao.TABLE_NAME;
		return getJdbcTemplate().query(query, new RoleRowMapper());
	}
	
	
	private List<Pair<Long, Long>> loadAllPermissionRecordsRaw()
	{
		String query = "SELECT * FROM " + PERMISSIONS_TABLE_NAME;
		return getJdbcTemplate().query(query,new RolesAssignedToUsersRowMapping());
	}
	
	/*
	 	=======================================================================
	 	OTHER OPERATIONS
	 	=======================================================================
	*/
	
	@Override
	public void save(User item) 
	{
		String query = 
			"INSERT INTO " + getTableName() + " " +
			"(username, email, passwordHash, salt, firstname, surname) " +
			"VALUES (?, ?, ?, ?, ?, ?)";
		
		Object[] arguments =
		{
			item.getUsername(),
			item.getEmail(),
			item.getPasswordHash(),
			item.getSalt(),
			item.getFirstname(),
			item.getSurname()
		};
		
		getJdbcTemplate().update(query, arguments);
	}

	@Override
	public void update(User item) 
	{
		updateRaw(item);
		clearRolesMappingForUser(item);
		addAllRolesToRolesMappingForUser(item);
	}
		
	private void updateRaw(User user)
	{
		String query = 
			"UPDATE " + getTableName() +
			"SET username = ?, email = ?, firstname = ?, surname = ? " +
			"WHERE id = ?";
		
		Object[] arguments =
		{
			user.getUsername(),
			user.getEmail(),
			user.getFirstname(),
			user.getSurname(),
			user.getId()
		};
		
		getJdbcTemplate().update(query, arguments);
	}
	
	private void clearRolesMappingForUser(User user)
	{
		String query = "DELETE FROM " + PERMISSIONS_TABLE_NAME + " WHERE userId = ?";
		Object[] arguments = { user.getId()	};
		getJdbcTemplate().update(query, arguments);
	}
	
	private void addAllRolesToRolesMappingForUser(User user)
	{
		// TODO: zvazit, zda by se vyplatilo toto provest v jednom SQL statementu
	
		for (Role role : user.getRoles())
		{
			Object[] arguments =
			{
				user.getId(),
				role.getId()
			};
			
			getJdbcTemplate().update(
				"INSERT INTO " + PERMISSIONS_TABLE_NAME + " VALUES (?, ?)", 
				arguments
			);
		}
	}
}
