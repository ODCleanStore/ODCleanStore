/**
 * 
 */
package cz.cuni.mff.odcleanstore.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import cz.cuni.mff.odcleanstore.components.Triple;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
@WebService
public class Scraper {

	@WebMethod(operationName = "add", action = "urn:addNumbers")
	@WebResult(name = "return")
	public int getVersion(@WebParam(name = "gogo", mode = WebParam.Mode.OUT) Holder<Integer> a, Triple[] param) {
		System.out.println(param.length);
		a.value = 8;
		return 7;
	}
}
