package cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class PrefixRowMapper extends CustomRowMapper<Prefix>
{
	public Prefix mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Prefix
		(
			rs.getString("NS_PREFIX"),
			rs.getString("NS_URL")
		);
	}
}
