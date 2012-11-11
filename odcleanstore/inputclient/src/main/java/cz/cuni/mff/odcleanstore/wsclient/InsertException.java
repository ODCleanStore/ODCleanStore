package cz.cuni.mff.odcleanstore.wsclient;

/*
 
 */

/**
 * A exception arising from InputWS WebService.
 * 
 * Possible property values: <br>
 * <pre>
 * MESSAGE            ID    MOREINFO
 * 
 * "Service busy"       1   "Service busy"
 * "Bad credentials"    2   "Bad credentials"
 * "Not authorized"     3   "Not authorized"
 * "Duplicated uuid"    4   "Duplicated uuid"
 * "Uuid bad format"    5   "Uuid bad format"
 * "Other error"        7   "Other error"
 * "Fatal error"        8   "Fatal error"
 * "Metadata error"     9   detailed error description
 *
 * "Soap error"       252   Soap fault code description
 * "Http error"       253   Http error code
 * "Connection error" 254   Connection error
 * "Client error"     255   see include inner exception
  * </pre>
 * @author Petr Jerman
 */
public final class InsertException extends Exception {

    private static final long serialVersionUID = 2550516676285229902L;
    
    public static final int SOAP_ERROR = 252;
    public static final int HTTP_ERROR = 253; 
    public static final int CONNECTION_ERROR = 254;
    public static final int CLIENT_ERROR = 255;

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
     * Create InsertException object for exceptions from server.
     * 
     * @param id exception id
     * @param message exception message
     * @param moreInfo exception more info
     */
    public InsertException(int id, String message, String moreInfo) {
        super(message);
        this.id = id;
        this.moreInfo = moreInfo;
    }

    /**
     * Create InsertException object for client side exceptions.
     * 
     * @param e cause 
     */
    public InsertException(Exception e) {
        super("Client error", e);
        this.id = CLIENT_ERROR;
        this.moreInfo = e.getClass().getName() + " : " + e.getMessage();
    }
    
    /**
     * Create InsertException object for Http error.
     * 
     * @param httpErrorCode http error code 
     */
    public InsertException(int httpErrorCode) {
        super("Http error");
        this.id = HTTP_ERROR;
        this.moreInfo = String.valueOf(httpErrorCode);
    }
    
    /**
     * Create InsertException object for Soap error.
     * 
     * @param message soap error description 
     */
    public InsertException(String message) {
        super("Soap error");
        this.id = SOAP_ERROR;
        this.moreInfo = message;
    }
}
