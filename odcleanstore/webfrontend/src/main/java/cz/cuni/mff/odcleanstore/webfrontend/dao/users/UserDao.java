package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
	
	private static Logger logger = Logger.getLogger(UserDao.class);
	
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
	
	@Override
	public void save(User item) 
	{
		logger.debug("Saving new user.");
		
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
		logger.debug("Updating user: " + item.getId());
		
		updateUserProperties(item);
		clearRolesMappingForUser(item);
		addAllRolesToRolesMappingForUser(item);
	}

	@Override
	public List<User> loadAll() 
	{
		logger.debug("Loading all registered users.");
		
		Map<Long, User> usersMapping = fetchAllUsers();
		Map<Long, Role> rolesMapping = fetchAllRoles();

		List<Pair<Long, Long>> assignedRoles = fetchRolesToUsersMapping();
		
		// assign rules to users according to the assignment
		//
		for (Pair<Long, Long> assignment : assignedRoles)
		{
			User targetUser = usersMapping.get(assignment.getFirst());
			Role targetRole = rolesMapping.get(assignment.getSecond());
			
			targetUser.addRole(targetRole);
		}
		
		logger.debug("Registered users successfuly loaded.");
		return new LinkedList<User>(usersMapping.values());
	}

	@Override
	public User load(Long id) 
	{
		logger.debug("Loading registered user: " + id);
		
		User user = loadRaw(id);
		Map<Long, Role> rolesMapping = fetchAllRoles();
		
		List<Pair<Long, Long>> assignedRoles = fetchRolesToUsersMappingForUserId(id);
		
		for (Pair<Long, Long> assignment : assignedRoles)
		{
			assert assignment.getFirst() == id;
			
			Role targetRole = rolesMapping.get(assignment.getSecond());
			user.addRole(targetRole);
		}

		return user;
	}
	
	public User loadForUsername(String username)
	{
		User user = fetchUserForUsername(username);
		if (user == null)
			return null;
		
		Long userId = user.getId();
		
		Map<Long, Role> rolesMapping = fetchAllRoles();
		
		List<Pair<Long, Long>> assignedRoles = fetchRolesToUsersMappingForUserId(userId);
		
		for (Pair<Long, Long> assignment : assignedRoles)
		{
			assert assignment.getFirst() == userId;
			
			Role targetRole = rolesMapping.get(assignment.getSecond());
			user.addRole(targetRole);
		}

		return user;
	}
	
	private User fetchUserForUsername(String username)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE username = ?";
		Object[] arguments = { username };

		return (User) getJdbcTemplate().queryForObject(query, arguments, getRowMapper());
	}
	
	private Map<Long, User> fetchAllUsers()
	{
		logger.debug("Fetching user rows.");
		
		// fetch all users
		//
		List<User> registeredUsers = loadAllRaw();
		
		// convert fetched users to a map
		//
		Map<Long, User> mapping = new HashMap<Long, User>();
		
		for (User user : registeredUsers)
			mapping.put(user.getId(), user);
		
		return mapping;
	}
	
	private Map<Long, Role> fetchAllRoles()
	{
		logger.debug("Fetching role rows.");
		
		// fetch all roles
		//
		// TODO: doresit cross-DAO queries
		List<Role> registeredRoles = getJdbcTemplate().query
		(
			"SELECT * FROM DB.ODCLEANSTORE.ROLES", 
			new RoleRowMapper()
		);
		
		// convert fetched roles to a map
		//
		Map<Long, Role> mapping = new HashMap<Long, Role>();
		
		for (Role role : registeredRoles)
			mapping.put(role.getId(), role);
		
		return mapping;
	}
	
	private List<Pair<Long, Long>> fetchRolesToUsersMapping()
	{
		logger.debug("Fetching user-to-role mapping");
		
		return getJdbcTemplate().query
		(
			"SELECT * FROM " + PERMISSIONS_TABLE_NAME, 
			new RolesAssignedToUsersRowMapping()
		);
	}
	
	private List<Pair<Long, Long>> fetchRolesToUsersMappingForUserId(Long id)
	{
		String query = "SELECT * FROM " + PERMISSIONS_TABLE_NAME + " WHERE userId = ?";
		Object[] arguments = { id };
		
		return getJdbcTemplate().query(query, arguments, new RolesAssignedToUsersRowMapping());
	}
	
	private void updateUserProperties(User user)
	{
		logger.debug("Updating user properties for user: " + user.getId());
		
		String query = 
			"UPDATE DB.ODCLEANSTORE.USERS " +
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
		logger.debug("Clearing roles-to-users mapping for user: " + user.getId());
		
		Object[] arguments = 
		{
			user.getId()	
		};
		
		getJdbcTemplate().update(
			"DELETE FROM DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS WHERE userId = ?", 
			arguments
		);
	}
	
	private void addAllRolesToRolesMappingForUser(User user)
	{
		// TODO: zvazit, zda by se vyplatilo toto provest v jednom SQL statementu
	
		logger.debug("Adding configured roles to roles-to-users mapping for user: " + user.getId());
		
		for (Role role : user.getRoles())
		{
			Object[] arguments =
			{
				user.getId(),
				role.getId()
			};
			
			getJdbcTemplate().update(
				"INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (?, ?)", 
				arguments
			);
		}
	}
}
