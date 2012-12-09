package cz.cuni.mff.odcleanstore.linker.rules;

import java.math.BigDecimal;

/**
 * Business entity representing linkage rule output.
 * @author Tomas Soukup
 */
public class Output {
	BigDecimal minConfidence;
	BigDecimal maxConfidence;
	/**
	 * @return minimal confidence of links sent to this output
	 */
	public BigDecimal getMinConfidence() {
		return minConfidence;
	}
	/**
	 * @param minConfidence minimal confidence of links sent to this output
	 */
	public void setMinConfidence(BigDecimal minConfidence) {
		this.minConfidence = minConfidence;
	}
	/**
	 * @return maximal confidence of links sent to this output
	 */
	public BigDecimal getMaxConfidence() {
		return maxConfidence;
	}
	/**
	 * @param maxConfidence maximal confidence of links sent to this output
	 */
	public void setMaxConfidence(BigDecimal maxConfidence) {
		this.maxConfidence = maxConfidence;
	}
}
