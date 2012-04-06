/**
 * 
 */
package cz.cuni.mff.odcleanstore.components.extractor;

import cz.cuni.mff.odcleanstore.components.Result;

/**
 * @author jermanp
 * 
 */
public class InputFromExtractorResult {
	public Result result;
	public String description;
	public String transactionuuid;

	public InputFromExtractorResult() {
	}

	public InputFromExtractorResult(Result result) {
		this.result = result;
	}
}
