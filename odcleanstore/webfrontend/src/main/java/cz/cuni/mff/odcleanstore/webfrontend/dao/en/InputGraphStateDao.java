package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraphState;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

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
	protected void deleteRaw(Integer item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
}
