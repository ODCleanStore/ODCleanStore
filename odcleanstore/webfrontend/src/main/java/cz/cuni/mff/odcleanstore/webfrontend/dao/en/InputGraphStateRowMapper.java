package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraphState;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CustomRowMapper;

public class InputGraphStateRowMapper extends CustomRowMapper<InputGraphState> {

	private static final long serialVersionUID = 1L;

	public InputGraphState mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new InputGraphState(
			rs.getInt("id"),
			blobToString(rs.getBlob("label"))
		);
	}

}
