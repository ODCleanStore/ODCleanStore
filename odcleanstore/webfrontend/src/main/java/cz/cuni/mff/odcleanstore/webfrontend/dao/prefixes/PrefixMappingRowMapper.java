package cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.PrefixMapping;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class PrefixMappingRowMapper extends CustomRowMapper<PrefixMapping>
{
	public PrefixMapping mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new PrefixMapping
		(
			rs.getString("NS_PREFIX"),
			rs.getString("NS_URL")
		);
	}
}
