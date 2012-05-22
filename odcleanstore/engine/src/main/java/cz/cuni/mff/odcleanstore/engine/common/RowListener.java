package cz.cuni.mff.odcleanstore.engine.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *  @author Petr Jerman
 */
public interface RowListener {
  void processRow(ResultSet resultSet, ResultSetMetaData resultSetMetaData) throws SQLException;
}
