package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.AttachedEngine;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class AttachedEngineDao extends DaoForEntityWithSurrogateKey<AttachedEngine> {
	//private static Logger logger = Logger.getLogger(OfficialPipelinesDao.class);
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "EN_ATTACHED_ENGINES";
	
	private ParameterizedRowMapper<AttachedEngine> rowMapper;
	
	public AttachedEngineDao() {
		rowMapper = new AttachedEngineRowMapper();
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<AttachedEngine> getRowMapper() {
		return rowMapper;
	}

	@Override
	public List<AttachedEngine> loadAll() {
		String query =
			"SELECT * FROM " + getTableName();		
		return jdbcQuery(query, getRowMapper());
	}
	
	@Override
	public List<AttachedEngine> loadAllBy (QueryCriteria criteria) {

		String query =
			"SELECT * FROM " + getTableName() +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, param, getRowMapper());
	}
}

