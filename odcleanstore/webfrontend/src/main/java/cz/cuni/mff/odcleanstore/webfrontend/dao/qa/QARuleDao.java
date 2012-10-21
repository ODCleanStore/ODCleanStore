package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CommittableDao;

/**
 * The QA rule DAO.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@CommittableDao(QARuleUncommittedDao.class)
public class QARuleDao extends AbstractRuleDao<QARule>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES";
	
	private ParameterizedRowMapper<QARule> rowMapper;
	
	/**
	 * 
	 */
	public QARuleDao()
	{
		this.rowMapper = new QARuleRowMapper();
	}

	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<QARule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public int getAuthorId(Integer entityId)
	{
		String query = "SELECT g.authorId " +
				"\n FROM " + getTableName() + " AS r JOIN " + QARulesGroupDao.TABLE_NAME + " AS g ON (g.id = r.groupId)" +
				"\n WHERE r.id = ?";
		return jdbcQueryForInt(query, entityId);
	}
}
