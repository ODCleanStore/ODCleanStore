package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class RedirectWithParamButtonWithLabel extends RedirectWithParamButton {
	private static final long serialVersionUID = 1L;
	
	private String label;

	public RedirectWithParamButtonWithLabel(final Class<? extends FrontendPage> redirectPage, 
			final Integer param, final String compName, final String label) {
		super(redirectPage, param, compName);
		
		this.label = label;
	}
	
	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		replaceComponentTagBody(markupStream, openTag, label);
	}
}
