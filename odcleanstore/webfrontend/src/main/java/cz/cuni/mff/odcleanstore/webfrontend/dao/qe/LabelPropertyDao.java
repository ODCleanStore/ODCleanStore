package cz.cuni.mff.odcleanstore.webfrontend.dao.qe;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qe.LabelProperty;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

/**
 * 
 * @author Dusan
 *
 */
public class LabelPropertyDao extends DaoForEntityWithSurrogateKey<LabelProperty>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "QE_LABEL_PROPERTIES";
	
	private ParameterizedRowMapper<LabelProperty> rowMapper;
	
	/**
	 * 
	 */
	public LabelPropertyDao()
	{
		this.rowMapper = new LabelPropertyRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<LabelProperty> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public void save(LabelProperty item) throws Exception
	{
		String query = "INSERT INTO " + getTableName() + " (property) VALUES (?)";
		Object[] params = { item.getProperty() };
		
		jdbcUpdate(query, params);
	}
}
