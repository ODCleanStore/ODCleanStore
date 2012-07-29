package cz.cuni.mff.odcleanstore.linker.exceptions;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

public class InvalidLinkageRuleException extends ODCleanStoreException {

	private static final long serialVersionUID = 7211480398423135895L;

	public InvalidLinkageRuleException(String message) {
		super(message);
	}

}
