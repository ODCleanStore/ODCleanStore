package cz.cuni.mff.odcleanstore.webfrontend.validators;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public abstract class CustomValidator implements IValidator<String>
{
	private static final String ERR_MSG_KEY_PREFIX = "form-validation.";
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(CustomValidator.class);
	
	public abstract void validate(IValidatable<String> validatable);

	protected void handleError(IValidatable<String> validatable, String errMsgKey)
	{
		handleError(validatable, errMsgKey, null);
	}
	
	protected void handleError(IValidatable<String> validatable, String errMsgKey, String message)
	{
		ValidationError error = new ValidationError();
		error.addMessageKey(ERR_MSG_KEY_PREFIX + errMsgKey);
		error.setVariable("message", message);
		validatable.error(error);
	}
}
