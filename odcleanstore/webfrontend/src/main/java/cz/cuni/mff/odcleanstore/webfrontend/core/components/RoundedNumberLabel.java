package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.basic.Label;

public class RoundedNumberLabel extends Label {

	private static final long serialVersionUID = 1L;
	private static final double PRECISION = 1000;
	
	public RoundedNumberLabel(String id, Double value) {
		this(id, value, PRECISION);
	}
	
	public RoundedNumberLabel(String id, Double value, Double precision) {
		super(id, ((Double)(Math.round(value * precision) / precision)).toString());
	}
}
