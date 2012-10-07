package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInError;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.GraphInErrorDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class GraphInErrorDetailPage extends FrontendPage {

	private static final long serialVersionUID = 1L;
	
	private GraphInErrorDao graphInErrorDao;

	public GraphInErrorDetailPage(final Integer id, final GraphsInErrorListPage referer) {
		super(
			"Home > Backend > Pipelines > Graphs in Error > Detail", 
			"Error Message"
		);
		
		graphInErrorDao = daoLookupFactory.getDao(GraphInErrorDao.class);
		
		List<GraphInError> graphs = graphInErrorDao.loadAllBy("eGraph.id", id);

		add(new Link<GraphInError>("back") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(referer);
			}
		});

		add(new Label("errorMessage", graphs.get(0).errorMessage));
	}

}
