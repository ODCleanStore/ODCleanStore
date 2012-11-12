package cz.cuni.mff.odcleanstore.engine.inputws;

public enum InputWSErrorEnumeration {
	SERVICE_BUSY,
	BAD_CREDENTIALS,
	NOT_AUTHORIZED,
	DUPLICATED_UUID,
	UUID_BAD_FORMAT,
	UNKNOWN_PIPELINENAME,
	OTHER_ERROR,
	FATAL_ERROR;
	
	private static final String[] MESSAGES = new String[] {
		"Service busy",
		"Bad credentials",
		"Not authorized",
		"Duplicated uuid",
		"Uuid bad format",
		"Unknown pipeline name",
		"Other error",
		"Fatal error"
	};
	
	public static String getMessage(InputWSErrorEnumeration iwe) {
		return MESSAGES[iwe.ordinal()];
	}
}
