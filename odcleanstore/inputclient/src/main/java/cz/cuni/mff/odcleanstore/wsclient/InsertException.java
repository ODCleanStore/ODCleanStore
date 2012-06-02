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
 * @author Petr Jerman
 */
public final class InsertException extends Exception {

	private static final long serialVersionUID = 1L;

	private int _id;
	private String _moreInfo;

	/**
	 * Get exception id. 
	 * @return exception id
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Get detailed exception information.
	 * @return detailed exception information
	 */
	public String getMoreInfo() {
		return _moreInfo;
	}

	InsertException(int id, String message, String moreInfo) {
		super(message);
		_id = id;
		_moreInfo = moreInfo;
	}
}
