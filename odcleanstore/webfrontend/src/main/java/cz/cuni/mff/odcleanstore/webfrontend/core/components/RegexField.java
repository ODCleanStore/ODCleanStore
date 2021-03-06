package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.webfrontend.validators.RegexValidator;

/**
 * Input component with validation for regexp supported in Virtuoso functions 
 * 
 * @author Jakub Daniel
 */
public class RegexField extends CustomValidableField {

	private static final long serialVersionUID = 1L;

	public RegexField(String id, JDBCConnectionCredentials credentials) {
		super(id);
		add(new RegexValidator(credentials));
	}

	public RegexValidator getRegexValidator() {
		return (RegexValidator)getCustomValidator(RegexValidator.class);
	}
}
