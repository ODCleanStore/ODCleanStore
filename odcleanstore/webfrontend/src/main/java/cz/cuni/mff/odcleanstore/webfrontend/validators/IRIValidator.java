package cz.cuni.mff.odcleanstore.webfrontend.validators;

import java.util.regex.Pattern;

import org.apache.wicket.validation.IValidatable;

/**
 * Ensures that a component accepts only well-formated IRIs.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class IRIValidator extends CustomValidator
{
	private static final Pattern IRI_PATTERN = Pattern.compile("^[^<>\"{}|^`\\x00-\\x20]*$");
	private static final long serialVersionUID = 1L;
	
	@Override
	public void validate(IValidatable<String> validatable) 
	{
		String iriValue = validatable.getValue();
		if (!isValidIRI(iriValue))
			handleError(validatable, "invalid-iri");
	}
	
	/**
	 * Returns true if and only if the given string represents a valid IRI.
	 * 
	 * @param iri
	 * @return
	 */
	public static boolean isValidIRI(String iri)
	{
		return !iri.isEmpty() && IRI_PATTERN.matcher(iri).matches();
	}
}
