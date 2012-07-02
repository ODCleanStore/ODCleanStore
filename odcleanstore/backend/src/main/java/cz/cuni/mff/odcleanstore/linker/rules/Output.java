package cz.cuni.mff.odcleanstore.linker.rules;

import java.math.BigDecimal;

public class Output {
	BigDecimal minConfidence;
	BigDecimal maxConfidence;
	public BigDecimal getMinConfidence() {
		return minConfidence;
	}
	public void setMinConfidence(BigDecimal minConfidence) {
		this.minConfidence = minConfidence;
	}
	public BigDecimal getMaxConfidence() {
		return maxConfidence;
	}
	public void setMaxConfidence(BigDecimal maxConfidence) {
		this.maxConfidence = maxConfidence;
	}
}
