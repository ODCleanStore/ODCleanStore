package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.validators.ReplacementValidator;

public class ReplacementField extends CustomValidableField {

	private static final long serialVersionUID = 1L;

	public ReplacementField(String id, RegexField regexField) {
		super(id);
		add(new ReplacementValidator(regexField));
	}

	public ReplacementValidator getRegexValidator() {
		return (ReplacementValidator)getCustomValidator(ReplacementValidator.class);
	}
}
