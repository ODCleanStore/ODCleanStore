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
	public InputFromExtractorResult InsertOnce(InputFromExtractorMetadata metadata, Triple[] triples);
	
	/**
	 * 
	 * @param metadata
	 * @param triples
	 * @return InputFromExctractorResult
	 */
	public InputFromExtractorResult BeginTransaction(InputFromExtractorMetadata metadata, Triple[] triples);
	
	/**
	 * 
	 * @param transactionuuid
	 * @param triples
	 * @return InputFromExctractorResult
	 */
	public InputFromExtractorResult MoreData(String transactionuuid, Triple[] triples);
	
	/**
	 * 
	 * @param transactionuuid
	 * @return InputFromExctractorResult
	 */
	public InputFromExtractorResult Commit(String transactionuuid); 
}
