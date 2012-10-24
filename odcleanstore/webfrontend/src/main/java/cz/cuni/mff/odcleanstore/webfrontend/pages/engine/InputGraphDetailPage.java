package cz.cuni.mff.odcleanstore.webfrontend.pages.engine;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraph;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.BooleanLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.StateLabel;
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
		
		addDetail(graphId);
	}

	private void addDetail (final Integer graphId) {
		setDefaultModel(createModelForOverview(inputGraphDao, graphId));
		
		add(new Label("UUID"));
		add(new Label("engineUUID"));
		add(new Label("pipelineLabel"));
		add(new StateLabel("stateLabel"));
		add(new BooleanLabel("isInCleanDB"));
		add(new TimestampLabel("updated"));
		
		ResourceStreamResource resource = null;
		
		try {
			resource = new ResourceStreamResource(new FileResourceStream(inputGraphDao.getContentFile(graphId)));
		} catch (Exception e) {
			getSession().error("Cannot dump graph");
			logger.error(e.getMessage());
			
			resource = new ResourceStreamResource(new StringResourceStream(""));
		}
		
		add(new ResourceLink<InputGraph>("dump", resource));
		
		//DELETE FILE?
	}
}
