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
	public InputFromExctractorResult InsertOnce(InputFromExtractorMetadata metadata, Triple[] triples);
	public InputFromExctractorResult BeginTransaction(InputFromExtractorMetadata metadata, Triple[] triples);
	public InputFromExctractorResult MoreData(String transactionuuid, Triple[] triples);
	public InputFromExctractorResult Commit(String transactionuuid); 
}
