package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInError;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class GraphInErrorRowMapper extends CustomRowMapper<GraphInError> {

	private static final long serialVersionUID = 1L;

	public GraphInError mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new GraphInError(
				rs.getInt("engineId"),
				rs.getInt("pipelineId"),
				blobToString(rs.getBlob("uuid")),
				rs.getInt("stateId"),
				rs.getInt("errorTypeId"),
				blobToString(rs.getBlob("errorMessage")),
				rs.getBoolean("isInCleanDB"),

				rs.getString("engineUuid"),
				blobToString(rs.getBlob("pipelineLabel")),
				blobToString(rs.getBlob("stateLabel")),
				blobToString(rs.getBlob("errorTypeLabel")));
	}

}
