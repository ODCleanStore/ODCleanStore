package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

/**
 * PagingNavigator that is hidden when pagination is not needed.
 * @author Jan Michelfeit
 */
public class UnobtrusivePagingNavigator extends PagingNavigator {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param id See Component
	 * @param pageable The pageable component the page links are referring to.
	 */
	public UnobtrusivePagingNavigator(final String id, final IPageable pageable)
	{
		super(id, pageable);
	}

	/**
	 * Constructor.
	 * @param id See Component
	 * @param pageable The pageable component the page links are referring to.
	 * @param labelProvider The label provider for the link text.
	 */
	public UnobtrusivePagingNavigator(final String id, final IPageable pageable, final IPagingLabelProvider labelProvider)
	{
		super(id, pageable, labelProvider);
	}
	
	@Override
	public boolean isVisible() {
		return this.getPageable().getPageCount() > 1;
	}
}
