package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.AttachedEngine;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class AttachedEngineRowMapper extends CustomRowMapper<AttachedEngine> {

	private static final long serialVersionUID = 1L;

	public AttachedEngine mapRow(ResultSet rs, int rowNum) throws SQLException {

		return new AttachedEngine
		(
				rs.getInt("id"),
				rs.getString("uuid"),
				rs.getBoolean("isPipelineError"),
				rs.getBoolean("isNotifyRequired"),
				blobToString(rs.getBlob("stateDescription")),
				rs.getTimestamp("updated")
		);
	}

}
