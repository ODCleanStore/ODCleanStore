package cz.cuni.mff.odcleanstore.linker.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

public class LinkerDao {
	
	private static LinkerDao dao;
	private static Connection connection;
	
	private LinkerDao(SparqlEndpoint endpoint) throws SQLException {
		connection = DriverManager.getConnection(endpoint.getUri(), endpoint.getUsername(), endpoint.getPassword());
	}
	
	public static LinkerDao getInstance(SparqlEndpoint endpoint) throws SQLException {
		if (dao == null) {
			return new LinkerDao(endpoint);
		}
		return dao;
	}
	
	public List<String> loadRules(String[] groups) throws SQLException {
		List<String> ruleList = new ArrayList<String>();
		Statement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery("select definition from OI_RULES where groupId in " + createInPart(groups));
			while (resultSet.next()) {
				ruleList.add(resultSet.getString("definition"));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {}
			}
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {}
			}
		}
		
		return ruleList;
	}
	
	private String createInPart(String[] groups) {
		String result = "(";
		for (String group : groups) {
			result += group + ",";
		}
		return result.substring(0, result.length()-1) + ")";
	}
	
	public List<RDFprefix> loadPrefixes() throws SQLException {
		List<RDFprefix> prefixList = new ArrayList<RDFprefix>();
		Statement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = connection.createStatement();
			resultSet = stmt.executeQuery("select * from DB.DBA.SYS_XML_PERSISTENT_NS_DECL");
			while (resultSet.next()) {
				RDFprefix prefix = new RDFprefix(resultSet.getString("NS_PREFIX"), resultSet.getString("NS_URL"));
				prefixList.add(prefix);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {}
			}
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {}
			}
		}
		return prefixList;
	}
}
