package cz.cuni.mff.odcleanstore.webfrontend.dao.cr;

import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AggregationTypeRowMapper extends CustomRowMapper<AggregationType>
{
	private static final long serialVersionUID = 1L;

	public AggregationType mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new AggregationType(
			rs.getLong("id"),
			rs.getString("label"),
			blobToString(rs.getBlob("description"))
		);
	}
}
