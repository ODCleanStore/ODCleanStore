package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CommittableDao;

/**
 * The DN rule DAO for uncommitted DN rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@CommittableDao(DNRuleUncommittedDao.class)
public class DNRuleDao extends AbstractRuleDao<DNRule>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULES";
	
	private ParameterizedRowMapper<DNRule> rowMapper;
	
	/**
	 * 
	 */
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
	public int getAuthorId(Integer entityId)
	{
		String query = "SELECT g.authorId " +
				"\n FROM " + getTableName() + " AS r JOIN " + DNRulesGroupDao.TABLE_NAME + " AS g ON (g.id = r.groupId)" +
				"\n WHERE r.id = ?";
		return jdbcQueryForInt(query, entityId);
	}
}
