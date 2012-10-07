package cz.cuni.mff.odcleanstore.wsclient;

/*
 
 */

/**
 * A exception arising from InputWS WebService.
 * 
 * Possible property values:
 * 
 * message : "Service busy", id : 1, moreInfo : "Service busy"
 * message : "Bad credentials", id :  2, moreInfo :  "Bad credentials"
 * message : "Not authorized", id :  3, moreInfo :  "Not authorized"
 * message : "Duplicated uuid", id :  4, moreInfo :  "Duplicated uuid"
 * message : "Uuid bad format", id :  5, moreInfo :  "Uuid bad format"
 * message : "Other error", id :  7, moreInfo :  "Other error"
 * message : "Fatal error", id :  8, moreInfo :  "Fatal error"
 * message : "Metadata error", id :  9, moreInfo :  detailed error description
 * 
 * message : "UnknownHost", id :  128, moreInfo :  "UnknownHost"
 * message : "Connection error", id :  129, moreInfo :  "Connection error"
 * message : "Connection error", id :  130, moreInfo :  "Response timeout" 
 *
 * @author Petr Jerman
 */
public final class InsertException extends Exception {

	private static final long serialVersionUID = 2550516676285229902L;

	private int id;
	private String moreInfo;

	/**
	 * Get exception id. 
	 * 
	 * @return exception id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get detailed exception information.
	 * 
	 * @return detailed exception information
	 */
	public String getMoreInfo() {
		return moreInfo;
	}

	/**
	 * Create InsertException object.
	 * 
	 * @param id exception id
	 * @param message exception message
	 * @param moreInfo exception more info
	 */
	InsertException(int id, String message, String moreInfo) {
		super(message);
		this.id = id;
		this.moreInfo = moreInfo;
	}
}
