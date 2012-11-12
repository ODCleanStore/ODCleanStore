package cz.cuni.mff.odcleanstore.webfrontend.pages.engine;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.model.EnumGraphState;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraph;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedLink;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.BooleanLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.StateLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TemporaryFileResourceLink;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TimestampLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.InputGraphDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC, Role.ADM })
public class InputGraphsPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(InputGraphsPage.class);

	private InputGraphDao inputGraphDao;
	private EngineOperationsDao engineOperationsDao;
	
	// Disable showing engine UUID while there is only one engine instance allowed
	// May change in the future
	private static final boolean showEngineUUID = false;
	
	public InputGraphsPage() 
	{
		super
		(
			"Home > Backend > Engine > Graphs", 
			"Graphs"
		);
		
		// prepare DAO objects
		//
		inputGraphDao = daoLookupFactory.getDao(InputGraphDao.class);
		engineOperationsDao = daoLookupFactory.getDao(EngineOperationsDao.class);
		
		// register page components
		//
		addHelpWindow(new InputGraphsHelpPanel("content"));
		addInputGraphs();
	}

	private void addInputGraphs() {
		DependentSortableDataProvider<InputGraph> data =
				new DependentSortableDataProvider<InputGraph>(inputGraphDao, "updated", SortOrder.DESCENDING);
			
		DataView<InputGraph> dataView = new DataView<InputGraph>("graphs", data)
		{
			private static final long serialVersionUID = 1L;
				
			@Override
			protected void populateItem(Item<InputGraph> item) {
				final InputGraph inputGraph = item.getModelObject(); 
				
				boolean isAuthorizedForPipeline = AuthorizationHelper.isAuthorizedForEntityEditing(inputGraph.getPipelineAuthorId());

				item.setModel(new CompoundPropertyModel<InputGraph>(inputGraph));
				
				final boolean link = inputGraph.getStateLabel().equals(EnumGraphState.FINISHED.name());

				item.add(new Label("UUIDLabel", ODCSInternal.dataGraphUriPrefix + inputGraph.UUID) {

					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return !link;
					}
				});
				item.add(createDumpDownloadLink("UUIDLink", inputGraph, link));
				item.add(new StateLabel("stateLabel"));
				item.add(new Label("engineUUID") {

					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return showEngineUUID;
					}
					
				});
				item.add(new Label("pipelineLabel"));
				item.add(new BooleanLabel("isInCleanDB"));
				item.add(new TimestampLabel("updated"));
				
				item.add(new RedirectWithParamButton(InputGraphDetailPage.class, inputGraph.getId(), "detail"));
				
				item.add(new AuthorizedLink<InputGraph>("rerunGraph", isAuthorizedForPipeline) {

					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisibleAuthorized() {
						return inputGraph.getStateLabel().equals(EnumGraphState.FINISHED.name())
							|| inputGraph.getStateLabel().equals(EnumGraphState.WRONG.name());
					}

					@Override
					public void onClickAuthorized() {
						try {
							engineOperationsDao.rerunGraph(inputGraph.getId());
						} catch (Exception e) {
							logger.error(e.getMessage());
							
							getSession().error(
								"The graph could not be rerun."
							);
						}
					}
				});
				
				item.add(new AuthorizedDeleteButton<InputGraph>(inputGraphDao, inputGraph.getId(), isAuthorizedForPipeline,
					"graph", new DeleteConfirmationMessage("graph"), InputGraphsPage.this)
				{

					private static final long serialVersionUID = 1L;

					@Override
					public boolean isVisible()
					{
						if (!isAuthorized)
						{
							return false;
						}
						return inputGraph.getStateLabel().equals(EnumGraphState.FINISHED.name())
							|| inputGraph.getStateLabel().equals(EnumGraphState.WRONG.name());
					}

					@Override
					public void delete() throws Exception
					{
						if (isAuthorized)
						{
							engineOperationsDao.queueGraphForDeletion(inputGraph.getId());
						}
					}
				});
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<InputGraph>("sortByUUID", "uuid", data, dataView));
		add(new SortTableButton<InputGraph>("sortByState", "stateLabel", data, dataView));
		add(new SortTableButton<InputGraph>("sortByEngine", "engineUuid", data, dataView) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isVisible() {
				return showEngineUUID;
			}
			
		});
		add(new SortTableButton<InputGraph>("sortByPipeline", "pipelineLabel", data, dataView));
		add(new SortTableButton<InputGraph>("sortByIsInCleanDB", "isInCleanDB", data, dataView));
		add(new SortTableButton<InputGraph>("sortByUpdated", "updated", data, dataView));
			
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
	private Component createDumpDownloadLink(String componentId, final InputGraph inputGraph, final boolean visible) {
		final String uri = ODCSInternal.dataGraphUriPrefix + inputGraph.UUID;

		TemporaryFileResourceLink.ITempFileCreator fileCreator = new TemporaryFileResourceLink.ITempFileCreator()
		{
			private static final long serialVersionUID = 1L;

			public File createTempFile()
			{
				try {
		            return inputGraphDao.getContentFile(inputGraph.getId());
		        } catch (Exception e) {
		            getSession().error("Cannot dump graph");
		            logger.error(e.getMessage());
		        }		
				return null;
			}
			
			public String getFileName()
			{
				return String.format("dump-%d.ttl", inputGraph.getId());
			}
		};

		return new TemporaryFileResourceLink<InputGraph>(componentId, "text/turtle", fileCreator) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
				replaceComponentTagBody(markupStream, openTag, uri);
			}
			
			@Override
			public boolean isVisible() {
				return visible;
			}
		};
	}
}
