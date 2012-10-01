package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CommittableDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

@CommittableDao(QARuleUncommittedDao.class)
public class QARuleDao extends DaoForAuthorableEntity<QARule>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES";
	//public static final String RESTRICTIONS_TABLE_NAME = TABLE_NAME_PREFIX + "QA_RULES_TO_PUBLISHERS_RESTRICTIONS";
	
	private ParameterizedRowMapper<QARule> rowMapper;
	
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
	protected void deleteRaw(Integer id) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	@Override
	public void save(QARule item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
	}
	
	public void update(QARule item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot modify " + getTableName() + ", use uncommitted version table instead");
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
