package cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

/**
 * The URI Prefix Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class PrefixRowMapper extends CustomRowMapper<Prefix>
{
	private static final long serialVersionUID = 1L;
	
	public Prefix mapRow(ResultSet rs, int rowNum) throws SQLException 
	{
		return new Prefix
		(
			rs.getString("NS_PREFIX"),
			rs.getString("NS_URL")
		);
	}
}
