/**
 * 
 */
package cz.cuni.mff.odcleanstore.webservices.scraper;

import javax.jws.WebService;
import javax.jws.WebParam;
import javax.jws.WebResult;

import cz.cuni.mff.odcleanstore.components.Result;
import cz.cuni.mff.odcleanstore.components.Triple;
import cz.cuni.mff.odcleanstore.components.extractor.InputFromExctractorResult;
import cz.cuni.mff.odcleanstore.components.extractor.InputFromExtractor;
import cz.cuni.mff.odcleanstore.components.extractor.InputFromExtractorMetadata;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
@WebService
public class Scraper implements InputFromExtractor {

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.components.extractor.InputFromExtractor#InsertOnce(cz.cuni.mff.odcleanstore.components
	 *      .extractor.InputFromExtractorMetadata, cz.cuni.mff.odcleanstore.components.Triple[])
	 */
	@Override
	@WebResult(name = "InputFromExctractorResult")
	public InputFromExctractorResult InsertOnce(@WebParam(name = "metadata") InputFromExtractorMetadata metadata,
			@WebParam(name = "triples") Triple[] triples) {
		return new InputFromExctractorResult(Result.OK);
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.components.extractor.InputFromExtractor#BeginTransaction(cz.cuni.mff.odcleanstore.components
	 *      .extractor.InputFromExtractorMetadata, cz.cuni.mff.odcleanstore.components.Triple[])
	 */
	@Override
	@WebResult(name = "InputFromExctractorResult")
	public InputFromExctractorResult BeginTransaction(@WebParam(name = "metadata") InputFromExtractorMetadata metadata,
			@WebParam(name = "triples") Triple[] triples) {
		return new InputFromExctractorResult(Result.ERROR);
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.components.extractor.InputFromExtractor#MoreData(java.lang.String,
	 *      cz.cuni.mff.odcleanstore.components.Triple[])
	 */
	@Override
	@WebResult(name = "InputFromExctractorResult")
	public InputFromExctractorResult MoreData(@WebParam(name = "transactionuuid") String transactionuuid,
			@WebParam(name = "triples") Triple[] triples) {
		return new InputFromExctractorResult(Result.ERROR);
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.components.extractor.InputFromExtractor#Commit(java.lang.String)
	 */
	@Override
	@WebResult(name = "InputFromExctractorResult")
	public InputFromExctractorResult Commit(@WebParam(name = "transactionuuid") String transactionuuid) {
		return new InputFromExctractorResult(Result.ERROR);
	}
}
