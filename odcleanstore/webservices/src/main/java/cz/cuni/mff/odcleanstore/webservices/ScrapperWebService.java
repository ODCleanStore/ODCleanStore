/**
 * 
 */
package cz.cuni.mff.odcleanstore.webservices;

import javax.jws.WebService;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
@WebService
public class ScrapperWebService {

	public int getVersion(int param) {
		return param;
	}
}
