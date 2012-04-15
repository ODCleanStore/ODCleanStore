/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.InsertResult;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.InsertResultStatus;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Metadata;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
@WebService
public class Scraper implements IScraper {

	@Override
	@WebResult(name = "InsertResult")
	public InsertResult insert(@WebParam(name = "user") String user, @WebParam(name = "password") String password,
			@WebParam(name = "metadata") Metadata metadata, @WebParam(name = "rdfXmlPayload") String rdfXmlPayload) {
		return new InsertResult(InsertResultStatus.OTHER_ERROR);
	}

}
