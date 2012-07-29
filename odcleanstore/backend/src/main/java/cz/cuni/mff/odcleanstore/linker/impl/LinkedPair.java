package cz.cuni.mff.odcleanstore.linker.impl;

public class LinkedPair {
	private String firstLabel;
	private String firstUri;
	private String secondLabel;
	private String secondUri;
	private Double confidence;
	
	public LinkedPair(String firstUri, String secondUri, Double confidence) {
		this.firstUri = firstUri;
		this.secondUri = secondUri;
		this.confidence = confidence;
	}
	
	public String getFirstLabel() {
		return firstLabel;
	}
	public void setFirstLabel(String firstLabel) {
		this.firstLabel = firstLabel;
	}
	public String getFirstUri() {
		return firstUri;
	}

	public void setFirstUri(String firstUri) {
		this.firstUri = firstUri;
	}

	public String getSecondUri() {
		return secondUri;
	}

	public void setSecondUri(String secondUri) {
		this.secondUri = secondUri;
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
