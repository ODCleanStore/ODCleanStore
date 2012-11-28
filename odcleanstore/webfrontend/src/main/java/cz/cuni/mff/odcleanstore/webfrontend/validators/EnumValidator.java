package cz.cuni.mff.odcleanstore.webfrontend.validators;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;

/**
 * Validator, which checks if inputed value is one of given valid options.
 * @author Tomas Soukup
 */
public class EnumValidator extends CustomValidator 
{	
	private static final long serialVersionUID = 1L;
	private IModel<List<String>> validOptions;
	
	/**
	 * @param validOptions valid options
	 */
	public EnumValidator(IModel<List<String>> validOptions)
	{
		this.validOptions = validOptions;
	}
	
	@Override
	public void validate(IValidatable<String> validatable) {
		if (!validOptions.getObject().contains(validatable.getValue()))
		{
			handleError(validatable, "not-in-list");
		}
	}
}
