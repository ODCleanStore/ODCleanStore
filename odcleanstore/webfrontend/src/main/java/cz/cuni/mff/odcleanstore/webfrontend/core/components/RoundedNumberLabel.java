package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.basic.Label;

/**
 * Allows to display floating point numbers with limited number of decimal places
 * @author Jakub Daniel
 */
public class RoundedNumberLabel extends Label {

	private static final long serialVersionUID = 1L;
	private static final double PRECISION = 1000;
	
	/**
	 * Display number rounded to 3 decimal places
	 * @param id component to attach to
	 * @param value value to be rounded
	 */
	public RoundedNumberLabel(String id, Double value) {
		this(id, value, PRECISION);
	}
	
	/**
	 * Display number rounded to log10(precision) decimal places
	 * @param id component to attach to
	 * @param value value to be rounded
	 * @param precision the precision
	 */
	public RoundedNumberLabel(String id, Double value, Double precision) {
		super(id, ((Double)(Math.round(value * precision) / precision)).toString());
	}
}
