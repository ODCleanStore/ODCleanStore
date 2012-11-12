package cz.cuni.mff.odcleanstore.webfrontend.dao.users;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.util.EmptyCodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.util.Pair;

/**
 * The User DAO.
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
	
	/**
	 * 
	 */
	public UserDao()
	{
		this.rowMapper = new UserRowMapper();
	}
	
	@Override
	public String getTableName() 
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
	 	LOAD A SINGLE ROW
	 	=======================================================================
	*/
	
	@Override
	protected User postLoadBy(User user)
	{
		user.setRoles(loadRolesForUser(user.getId()));
		return user;
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	private Set<Role> loadRolesForUser(Integer userId)
	{
		String query = 
			"SELECT * FROM " + PERMISSIONS_TABLE_NAME + " AS P " +
			"JOIN " + RoleDao.TABLE_NAME + " AS R ON R.id = P.roleId " +
			"WHERE userId = ?";
		
		Object[] params = { userId };
		
		List<Role> rolesList = jdbcQuery(query, params, new RoleRowMapper());
		return new HashSet<Role>(rolesList);
	}
	
	/*
	 	=======================================================================
	 	LOAD ALL ROWS
	 	=======================================================================
	*/
	
	@Override
	protected List<User> postLoadAllBy(List<User> users)
	{
		Map<Integer, User> usersMapping = convertListToHashMap(users);
		
		Map<Integer, Role> rolesMapping = convertListToHashMap(loadAllRolesRaw());

		List<Pair<Integer, Integer>> assignedRoles = loadAllPermissionRecordsRaw();
		
		// assign rules to users according to the assignment
		for (Pair<Integer, Integer> assignment : assignedRoles)
		{
			User targetUser = usersMapping.get(assignment.getFirst());
			Role targetRole = rolesMapping.get(assignment.getSecond());
			
			targetUser.addRole(targetRole);
		}
		
		return users;
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private <T extends EntityWithSurrogateKey> Map<Integer, T> convertListToHashMap(List<T> list)
	{
		Map<Integer, T> mapping = new HashMap<Integer, T>();
		
		for (T item : list)
			mapping.put(item.getId(), item);
		
		return mapping;
	}
	
	/**
	 * 
	 * @return
	 */
	private List<Role> loadAllRolesRaw()
	{
		return getLookupFactory().getDao(RoleDao.class).loadAll();
	}
	
	/**
	 * 
	 * @return
	 */
	private List<Pair<Integer, Integer>> loadAllPermissionRecordsRaw()
	{
		String query = "SELECT * FROM " + PERMISSIONS_TABLE_NAME;
		return jdbcQuery(query,new RolesAssignedToUsersRowMapper());
	}
	
	/*
	 	=======================================================================
	 	OTHER OPERATIONS
	 	=======================================================================
	*/
	
	/**
	 * 
	 * @param item
	 * @param doAfter
	 * @throws Exception
	 */
	public void save(final User item, final CodeSnippet doAfter) throws Exception {
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				saveRaw(item);
				doAfter.execute();
			}
		});
	}
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	private void saveRaw(User item) throws Exception
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
		
		jdbcUpdate(query, arguments);
	}

	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void update(User item) throws Exception
	{
		update(item, new EmptyCodeSnippet());
	}
	
	/**
	 * 
	 * @param item
	 * @param doAfter
	 * @throws Exception
	 */
	public void update(final User item, final CodeSnippet doAfter) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				updateRaw(item);
				clearRolesMappingForUser(item);
				addAllRolesToRolesMappingForUser(item);
				doAfter.execute();
			}
		});
	}
	
	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	private void updateRaw(User user) throws Exception
	{
		String query = 
			"UPDATE " + getTableName() + " " +
			"SET username = ?, email = ?, firstname = ?, surname = ?, passwordHash = ?, salt = ? " +
			"WHERE id = ?";
		
		Object[] arguments =
		{
			user.getUsername(),
			user.getEmail(),
			user.getFirstname(),
			user.getSurname(),
			user.getPasswordHash(),
			user.getSalt(),
			user.getId()
		};
		
		jdbcUpdate(query, arguments);
	}
	
	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	private void clearRolesMappingForUser(User user) throws Exception
	{
		String query = "DELETE FROM " + PERMISSIONS_TABLE_NAME + " WHERE userId = ?";
		Object[] arguments = { user.getId()	};
		jdbcUpdate(query, arguments);
	}
	
	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	private void addAllRolesToRolesMappingForUser(User user) throws Exception
	{
		for (Role role : user.getRoles())
		{
			Object[] arguments =
			{
				user.getId(),
				role.getId()
			};
			
			jdbcUpdate(
				"INSERT INTO " + PERMISSIONS_TABLE_NAME + " VALUES (?, ?)", 
				arguments
			);
		}
	}
}
