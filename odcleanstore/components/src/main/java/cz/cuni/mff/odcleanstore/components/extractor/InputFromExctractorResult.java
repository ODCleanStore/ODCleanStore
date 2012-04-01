/**
 * 
 */
package cz.cuni.mff.odcleanstore.components.extractor;

import cz.cuni.mff.odcleanstore.components.Result;

/**
 * @author jermanp
 * 
 */
public class InputFromExctractorResult {
	public Result result;
	public String description;
	public String transactionuuid;

	public InputFromExctractorResult() {
	}

	public InputFromExctractorResult(Result result) {
		this.result = result;
	}
}
