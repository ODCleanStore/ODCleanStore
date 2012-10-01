package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CommittableDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

@CommittableDao(OIRuleUncommittedDao.class)
public class OIRuleDao extends DaoForAuthorableEntity<OIRule>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_RULES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<OIRule> rowMapper;
	
	public OIRuleDao()
	{
		this.rowMapper = new OIRuleRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<OIRule> getRowMapper() 
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
	public void save(OIRule item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	public void update(OIRule item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}

	@Override
	public int getAuthorId(Integer entityId)
	{
		String query = "SELECT g.authorId " +
				"\n FROM " + getTableName() + " AS r JOIN " + OIRulesGroupDao.TABLE_NAME + " AS g ON (g.id = r.groupId)" +
				"\n WHERE r.id = ?";
		return jdbcQueryForInt(query, entityId);
	}
}
