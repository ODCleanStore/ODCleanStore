package cz.cuni.mff.odcleanstore.webfrontend.validators;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;

public class IRIValidator extends CustomValidator
{
	private static final Pattern IRI_PATTERN = Pattern.compile("^[^<>\"{}|^`\\x00-\\x20]*$");
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(IRIValidator.class);
	
	@Override
	public void validate(IValidatable<String> validatable) 
	{
		String iriValue = validatable.getValue();
		if (!isValidIRI(iriValue))
			handleError(validatable, "invalid-iri");
	}
	
	public static boolean isValidIRI(String iri)
	{
		return !iri.isEmpty() && IRI_PATTERN.matcher(iri).matches();
	}
}
