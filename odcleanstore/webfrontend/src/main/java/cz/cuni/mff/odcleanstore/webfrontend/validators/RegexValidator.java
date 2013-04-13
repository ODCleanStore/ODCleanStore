package cz.cuni.mff.odcleanstore.webfrontend.validators;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

/**
 * Validator that attempts to use regexp
 * 
 * either alone or with replacement value (may check for back references or forbidden characters)
 * 
 * @author Jakub Daniel
 */
public class RegexValidator extends CustomValidator
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(RegexValidator.class);
	
	final JDBCConnectionCredentials credentials;
	
	public RegexValidator(JDBCConnectionCredentials credentials) {
		this.credentials = credentials;
	}
	
	@Override
	public void validate(IValidatable<String> validatable) 
	{
		String regexValue = validatable.getValue();
		
		try {
			validate(regexValue, "");
		} catch (ConnectionException e) {
			//Error in connection
			logger.error(e.getMessage());
		} catch (SQLException e) {
			//Error in connection
			logger.error(e.getMessage());
		} catch (QueryException e) {
			handleError(validatable, "invalid-regex");
		}
	}
	
	public void validate(String regexValue, String replacementValue) throws ConnectionException, QueryException, SQLException {
		String query = "SPARQL SELECT (fn:replace(str(''), ??, ??)) AS ?x WHERE {?s ?p ?o} LIMIT 1";
		Object[] param = {regexValue, replacementValue};

		VirtuosoConnectionFactory.createJDBCConnection(credentials).executeSelect(query, param).close();
	}
}