package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIFileFormat;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class OIFileFormatDao extends DaoForEntityWithSurrogateKey<OIFileFormat>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_FILE_FORMATS";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<OIFileFormat> rowMapper;
	
	public OIFileFormatDao()
	{
		this.rowMapper = new OIFileFormatRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected ParameterizedRowMapper<OIFileFormat> getRowMapper() 
	{
		return this.rowMapper;
	}
	
	@Override
	protected void deleteRaw(Integer item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
}
