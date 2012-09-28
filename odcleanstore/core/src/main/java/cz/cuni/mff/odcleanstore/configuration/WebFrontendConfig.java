package cz.cuni.mff.odcleanstore.configuration;

import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

/**
 * Encapsulates Web Frontend configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Web Frontend configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Tomas Soukup
 *
 */
public class WebFrontendConfig extends ConfigGroup {
	 /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "web_frontend" + NAME_DELIMITER;
    
    private final JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials;
    private final JDBCConnectionCredentials cleanDBJDBCConnectionCredentials;
    
    private final String gmailAddress;
    private final String gmailPassword;
    
    public WebFrontendConfig(JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials, 
            JDBCConnectionCredentials cleanDBJDBCConnectionCredentials,
            String gmailAddress, String gmailPassword) {
    	this.dirtyDBJDBCConnectionCredentials = dirtyDBJDBCConnectionCredentials;
    	this.cleanDBJDBCConnectionCredentials = cleanDBJDBCConnectionCredentials;
    	this.gmailAddress = gmailAddress;
    	this.gmailPassword = gmailPassword;
    }
    
    public static WebFrontendConfig load(Properties properties) 
    		throws ParameterNotAvailableException, IllegalParameterFormatException {
    	JDBCConnectionCredentials dirtyJDBCConnectionCredentials = 
                loadJDBCConnectionCredentials(properties,  EnumDbConnectionType.DIRTY);
    	JDBCConnectionCredentials cleanJDBCConnectionCredentials =
                loadJDBCConnectionCredentials(properties,  EnumDbConnectionType.CLEAN);
    	
    	ParameterFormat<String> formatString = new FormatString();
    	String gmailAddress = loadParam(properties, GROUP_PREFIX + "gmail_address", formatString);
    	String gmailPassword = loadParam(properties, GROUP_PREFIX + "gmail_password", formatString);
    	
    	return new WebFrontendConfig(dirtyJDBCConnectionCredentials, cleanJDBCConnectionCredentials,
    			gmailAddress, gmailPassword);
    }
    
	public JDBCConnectionCredentials getDirtyDBJDBCConnectionCredentials() {
		return dirtyDBJDBCConnectionCredentials;
	}
	public JDBCConnectionCredentials getCleanDBJDBCConnectionCredentials() {
		return cleanDBJDBCConnectionCredentials;
	}
	public String getGmailAddress() {
		return gmailAddress;
	}
	public String getGmailPassword() {
		return gmailPassword;
	}
}
