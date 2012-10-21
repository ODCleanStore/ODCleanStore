package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.wicket.markup.html.basic.Label;

public class TimestampLabel extends Label {

	private static final long serialVersionUID = 1L;

	public TimestampLabel(String compName) {
		super(compName);
	}

	public TimestampLabel(String compName, Timestamp timestamp) {
		super(compName, new SimpleDateFormat().format(timestamp));
	}
}
