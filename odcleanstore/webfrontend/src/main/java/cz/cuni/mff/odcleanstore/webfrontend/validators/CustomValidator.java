package cz.cuni.mff.odcleanstore.webfrontend.validators;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Abstract parent of all project-specific form validators.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class CustomValidator implements IValidator<String>
{
	private static final String ERR_MSG_KEY_PREFIX = "form-validation.";
	private static final long serialVersionUID = 1L;

	/**
	 * Validates the given component.
	 * 
	 * @param
	 */
	public abstract void validate(IValidatable<String> validatable);

	/**
	 * Handles the given error which occurred on the given component.
	 *  
	 * @param validatable
	 * @param errMsgKey
	 */
	protected void handleError(IValidatable<String> validatable, String errMsgKey)
	{
		handleError(validatable, errMsgKey, null);
	}
	
	/**
	 * Registers the given error message with the given component under the
	 * given key.
	 * 
	 * @param validatable
	 * @param errMsgKey
	 * @param message
	 */
	protected void handleError(IValidatable<String> validatable, String errMsgKey, String message)
	{
		ValidationError error = new ValidationError();
		error.addMessageKey(ERR_MSG_KEY_PREFIX + errMsgKey);
		error.setVariable("message", message);
		validatable.error(error);
	}
}
