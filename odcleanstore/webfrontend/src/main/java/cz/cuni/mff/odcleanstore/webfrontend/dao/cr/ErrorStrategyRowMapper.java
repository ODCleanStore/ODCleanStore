package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Error strategy Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ErrorStrategyRowMapper extends CustomRowMapper<ErrorStrategy>
{
	private static final long serialVersionUID = 1L;
	
	public ErrorStrategy mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new ErrorStrategy(
			rs.getInt("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
