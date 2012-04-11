/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.AuthenticateResult;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.AuthenticateResultStatus;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.InsertResult;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.InsertResultStatus;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Metadata;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Triple;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
@WebService
public class Scraper implements IScraper {

	/**
	 * @see cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	@WebResult(name = "AuthenticateResult")
	public AuthenticateResult authenticate(@WebParam(name = "user") String user,
			@WebParam(name = "password") String password) {
		return new AuthenticateResult(AuthenticateResultStatus.UNSPECIFIEDERROR, null, 65536);
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper#insertWithAutoCommit(java.lang.String,
	 *      cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Metadata,
	 *      cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Triple[])
	 */
	@Override
	@WebResult(name = "InsertResult")
	public InsertResult insertWithAutoCommit(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "metadata") Metadata metadata, @WebParam(name = "triples") Triple[] triples) {
		return new InsertResult(InsertResultStatus.UNSPECIFIEDERROR, null);
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper#beginInsertion(java.lang.String,
	 *      cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Metadata,
	 *      cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Triple[])
	 */
	@Override
	@WebResult(name = "InsertResult")
	public InsertResult beginInsertion(@WebParam(name = "sessionId") String sessionId,
			@WebParam(name = "metadata") Metadata metadata, @WebParam(name = "triples") Triple[] triples) {
		return new InsertResult(InsertResultStatus.UNSPECIFIEDERROR, null);
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper#moreData(java.lang.String,
	 *      cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Triple[])
	 */
	@Override
	@WebResult(name = "InsertResult")
	public InsertResult moreData(@WebParam(name = "graphName") String graphName,
			@WebParam(name = "triples") Triple[] triples) {
		return new InsertResult(InsertResultStatus.UNSPECIFIEDERROR, null);
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper#commit(java.lang.String)
	 */
	@Override
	@WebResult(name = "InsertResult")
	public InsertResult commit(@WebParam(name = "graphName") String graphName) {
		return new InsertResult(InsertResultStatus.UNSPECIFIEDERROR, null);
	}

}
