/**
 * 
 */
package cz.cuni.mff.odcleanstore.components.extractor;

import cz.cuni.mff.odcleanstore.components.Triple;

/**
 * @author jermanp
 *
 */
public interface InputFromExtractor {
	/**
	 * 
	 * @param metadata
	 * @param triples
	 * @return InputFromExctractorResult
	 */
	public InputFromExctractorResult InsertOnce(InputFromExtractorMetadata metadata, Triple[] triples);
	
	/**
	 * 
	 * @param metadata
	 * @param triples
	 * @return InputFromExctractorResult
	 */
	public InputFromExctractorResult BeginTransaction(InputFromExtractorMetadata metadata, Triple[] triples);
	
	/**
	 * 
	 * @param transactionuuid
	 * @param triples
	 * @return InputFromExctractorResult
	 */
	public InputFromExctractorResult MoreData(String transactionuuid, Triple[] triples);
	
	/**
	 * 
	 * @param transactionuuid
	 * @return InputFromExctractorResult
	 */
	public InputFromExctractorResult Commit(String transactionuuid); 
}
