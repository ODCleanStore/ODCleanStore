package cz.cuni.mff.odcleanstore.linker.impl;

public class LinkedPair {
	private String firstLabel;
	private String secondLabel;
	private Double confidence;
	
	public LinkedPair(String firstLabel, String secondLabel, Double confidence) {
		this.firstLabel = firstLabel;
		this.secondLabel = secondLabel;
		this.confidence = confidence;
	}
	
	public String getFirstLabel() {
		return firstLabel;
	}
	public void setFirstLabel(String firstLabel) {
		this.firstLabel = firstLabel;
	}
	public String getSecondLabel() {
		return secondLabel;
	}
	public void setSecondLabel(String secondLabel) {
		this.secondLabel = secondLabel;
	}
	public Double getConfidence() {
		return confidence;
	}
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
}
