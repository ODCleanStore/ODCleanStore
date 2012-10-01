package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraphState;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class InputGraphStateDao extends DaoForEntityWithSurrogateKey<InputGraphState> {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<InputGraphState> getRowMapper() {
		return new InputGraphStateRowMapper();
	}
	
	@Override
	public List<InputGraphState> loadAllBy (QueryCriteria criteria) {

		String query = "SELECT * FROM " + TABLE_NAME + criteria.buildWhereClause() + criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, param, getRowMapper());
	}
}
