package cz.cuni.mff.odcleanstore.webfrontend.dao.qe;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qe.LabelProperty;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * 
 * @author Dusan
 *
 */
public class LabelPropertyRowMapper extends CustomRowMapper<LabelProperty>
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public LabelProperty mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new LabelProperty(
			rs.getInt("id"), 
			rs.getString("property")
		);
	}
}
