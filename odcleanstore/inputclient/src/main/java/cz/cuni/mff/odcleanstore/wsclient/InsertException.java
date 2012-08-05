package cz.cuni.mff.odcleanstore.wsclient;

/*
 
 */

/**
 * A exception arising from InputWS WebService.</br>
 * </br>
 * Possible property values:
 * <ul>
 * <li>message : "Service busy", id : 1, moreInfo : "Service busy"</li>
 * <li>message : "Bad credentials", id :  2, moreInfo :  "Bad credentials"</li>
 * <li>message : "Not authorized", id :  3, moreInfo :  "Not authorized"</li>
 * <li>message : "Duplicated uuid", id :  4, moreInfo :  "Duplicated uuid"</li>
 * <li>message : "Uuid bad format", id :  5, moreInfo :  "Uuid bad format"</li>
 * <li>message : "Unknown pipeline name", id :  6, moreInfo :  "Unknown pipeline name"</li>
 * <li>message : "Other error", id :  7, moreInfo :  "Other error"</li>
 * <li>message : "Fatal error", id :  8, moreInfo :  "Fatal error"</li>
 * <li>message : "Metadata error", id :  9, moreInfo :  detailed error description</li>
 *</ul>
 * @author Petr Jerman
 */
public final class InsertException extends Exception {
	
	private static final long serialVersionUID = 2550516676285229902L;
	
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
