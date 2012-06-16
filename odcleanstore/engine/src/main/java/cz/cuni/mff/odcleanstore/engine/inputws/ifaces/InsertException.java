package cz.cuni.mff.odcleanstore.engine.inputws.ifaces;

/**
 * A exception arising from InputWS WebService.
 *
 * @author Petr Jerman
 */
public class InsertException extends Exception {

	private static final long serialVersionUID = 1L;

	// defined exceptions id 1 - 8
	public static final InsertException SERVICE_BUSY = new InsertException("Service busy", 1, "Service busy");
	public static final InsertException BAD_CREDENTIALS = new InsertException("Bad credentials", 2, "Bad credentials");
	public static final InsertException NOT_AUTHORIZED = new InsertException("Not authorized", 3, "Not authorized");
	public static final InsertException DUPLICATED_UUID = new InsertException("Duplicated uuid", 4, "Duplicated uuid");
	public static final InsertException UUID_BAD_FORMAT = new InsertException("Uuid bad format", 5, "Uuid bad format");
	public static final InsertException UNKNOWN_PIPELINENAME = new InsertException("Unknown pipeline name", 6, "Unknown pipeline name");
	public static final InsertException OTHER_ERROR = new InsertException("Other error", 7, "Other error");
	public static final InsertException FATAL_ERROR = new InsertException("Fatal error", 8, "Fatal error");
	
	/**
	 * Constructs a new metadata exception with the given info.
	 * 
	 * @param moreInfo the detail information
	 */
	public InsertException(String moreInfo) {
		super("Metadata error");
		this.id = 9;
		this.moreInfo = moreInfo;
	}
	
	private int id;
	private String moreInfo;

	private InsertException(String message, int id, String moreInfo) {
		super(message);
		this.id = id;
		this.moreInfo = moreInfo;
	}

	/**
	 * Get exception id. 
	 * @return exception id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get detailed exception information.
	 * @return detailed exception information
	 */
	public String getMoreInfo() {
		return moreInfo;
	}
}
