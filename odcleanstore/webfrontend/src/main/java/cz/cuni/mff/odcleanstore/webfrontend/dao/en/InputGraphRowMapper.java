package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraph;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class InputGraphRowMapper extends CustomRowMapper<InputGraph> {
	private static final long serialVersionUID = 1L;

	public InputGraph mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new InputGraph
		(
				rs.getInt("id"),
				blobToString(rs.getBlob("uuid")),
				rs.getInt("stateId"),
				rs.getBoolean("isInCleanDB"),
				rs.getInt("engineId"),
				rs.getInt("pipelineId"),
				rs.getTimestamp("updated"),
				
				blobToString(rs.getBlob("engineUuid")),
				blobToString(rs.getBlob("pipelineLabel")),
				blobToString(rs.getBlob("stateLabel"))
		);
	}

}
