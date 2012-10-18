package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RuleEntity;

/** 
 * Base DAO class for rules in a group (see {@link AbstractRulesGroupDao})
 * @author Jan Michelfeit
 *
 */
public abstract class AbstractRuleDao<T extends RuleEntity> extends DaoForAuthorableEntity<T>
{
	private static final long serialVersionUID = 1L;
	
	protected final String GROUP_ID_COLUMN = "groupId";
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	@Override
	public void save(T item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	public void update(T item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	/** 
	 * Commits changes in rules from uncommitted table to official table.
	 * This method call should be wrapped in a transaction; for that reason it has package visibility only
	 */
	/*package*/ final void commitChanges(final Integer groupId) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				commitChangesImpl(groupId);
			}
		});
	}
	
	protected void commitChangesImpl(Integer groupId) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot commit changes in " + getTableName() + ", use uncommitted version table instead");
	}
	
	public void deleteByGroup(Integer groupId) throws Exception
	{
		String query = "DELETE FROM " + getTableName() + " WHERE " + GROUP_ID_COLUMN + " = ?";
		jdbcUpdate(query, groupId);
	}
}
