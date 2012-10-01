package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

public class DNRuleDao extends DaoForAuthorableEntity<DNRule>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULES";
	
	private ParameterizedRowMapper<DNRule> rowMapper;
	
	public DNRuleDao()
	{
		this.rowMapper = new DNRuleRowMapper();
	}

	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNRule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	@Override
	public void save(DNRule item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	public void update(DNRule item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	@Override
	public int getAuthorId(Integer entityId)
	{
		String query = "SELECT g.authorId " +
				"\n FROM " + getTableName() + " AS r JOIN " + DNRulesGroupDao.TABLE_NAME + " AS g ON (g.id = r.groupId)" +
				"\n WHERE r.id = ?";
		return jdbcQueryForInt(query, entityId);
	}
}
