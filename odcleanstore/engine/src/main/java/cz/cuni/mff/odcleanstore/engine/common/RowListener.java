package cz.cuni.mff.odcleanstore.engine.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface RowListener {
  void processRow(ResultSet resultSet, ResultSetMetaData resultSetMetaData) throws SQLException;
}
