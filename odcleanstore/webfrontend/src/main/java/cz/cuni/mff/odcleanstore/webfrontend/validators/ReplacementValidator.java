package cz.cuni.mff.odcleanstore.webfrontend.validators;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;

import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RegexField;

/**
 * Validator which checks if inputed value matches replacement regex.
 * @author Jakub Daniel
 */
public class ReplacementValidator extends CustomValidator
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ReplacementValidator.class);
	
	final RegexField pattern;
	
	public ReplacementValidator(RegexField pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public void validate(IValidatable<String> validatable) 
	{
		String replacementValue = validatable.getValue();
		
		try {
			pattern.getRegexValidator().validate(pattern.getValue(), replacementValue);
		} catch (ConnectionException e) {
			//Error in connection
			logger.error(e.getMessage());
		} catch (SQLException e) {
			//Error in connection
			logger.error(e.getMessage());
		} catch (QueryException e) {
			handleError(validatable, "invalid-replacement");
		}
	}
}