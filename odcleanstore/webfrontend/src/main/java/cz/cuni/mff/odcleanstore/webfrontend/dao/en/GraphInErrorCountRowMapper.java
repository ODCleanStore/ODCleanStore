package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInErrorCount;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class GraphInErrorCountRowMapper extends CustomRowMapper<GraphInErrorCount> {

	private static final long serialVersionUID = 1L;

	public GraphInErrorCount mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		return new GraphInErrorCount(rs.getInt("pipelineId"),
				blobToString(rs.getBlob("pipelineLabel")),
				rs.getInt("graphCount"));
	}

}
