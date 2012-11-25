package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * Component that mixes ability to redirect to a concrete page and display label
 * known at construction time.
 * 
 * @author Jakub Daniel
 */
public class RedirectWithParamButtonWithLabel extends RedirectWithParamButton {
	private static final long serialVersionUID = 1L;
	
	private String label;

	public RedirectWithParamButtonWithLabel(final Class<? extends FrontendPage> redirectPage,
		final String compName, final String label, final Object... param) {
		super(redirectPage, compName, param);
		
		this.label = label;
	}
	
	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		replaceComponentTagBody(markupStream, openTag, label);
	}
}
