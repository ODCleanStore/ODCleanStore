package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidatorAdapter;

import cz.cuni.mff.odcleanstore.webfrontend.validators.CustomValidator;

/**
 * Textfield with custom validator.
 * @author Jakub Daniel
 */
public abstract class CustomValidableField extends TextField<String> {

	private static final long serialVersionUID = 1L;

	/**
	 * @param id
	 */
	public CustomValidableField(String id) {
		super(id);
	}
	
	/**
	 * @param validatorClass
	 * @return
	 */
	public CustomValidator getCustomValidator(Class<? extends CustomValidator> validatorClass) {
		for (IValidator<? super String> validator : getValidators()) {

			if (validatorClass.isInstance(validator)) return (CustomValidator)validator;

			if (validator instanceof ValidatorAdapter) {
				ValidatorAdapter<?> validatorAdapter = (ValidatorAdapter<?>)validator;

				if (validatorClass.isInstance(validatorAdapter.getValidator())) {
					return (CustomValidator)validatorAdapter.getValidator();
				}
			}
		}
		return null;
	}

}
