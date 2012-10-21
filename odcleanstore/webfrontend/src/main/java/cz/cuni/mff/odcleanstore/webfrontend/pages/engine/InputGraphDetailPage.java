package cz.cuni.mff.odcleanstore.webfrontend.pages.engine;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraph;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.BooleanLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TimestampLabel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.InputGraphDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class InputGraphDetailPage extends FrontendPage {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(InputGraphDetailPage.class);
	
	private InputGraphDao inputGraphDao;

	public InputGraphDetailPage(Integer graphId) {
		super
		(
			"Home > Backend > Engine > Graphs > Detail", 
			"Graph Detail"
		);
		
		inputGraphDao = daoLookupFactory.getDao(InputGraphDao.class);
		
		addDetail(inputGraphDao.load(graphId));
	}

	private void addDetail (final InputGraph inputGraph) {
		add(new Label("UUID", inputGraph.UUID));
		add(new Label("engineUUID", inputGraph.engineUUID));
		add(new Label("pipelineLabel", inputGraph.pipelineLabel));
		add(new Label("stateLabel", inputGraph.stateLabel));
		add(new BooleanLabel("isInCleanDB", inputGraph.isInCleanDB));
		add(new TimestampLabel("updated", inputGraph.updated));
		
		String content = "";
		
		try
		{
			content = inputGraphDao.getContent(inputGraph);
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			getSession().error("Could not retrieve the content of the graph.");
		}
		
		add(new Label("dump", content));
	}
}
