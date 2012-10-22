package cz.cuni.mff.odcleanstore.webfrontend.pages.engine;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.model.EnumGraphState;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.InputGraph;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
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
				new DependentSortableDataProvider<InputGraph>(inputGraphDao, "uuid");
			
		DataView<InputGraph> dataView = new DataView<InputGraph>("graphs", data)
		{
			private static final long serialVersionUID = 1L;
				
			@Override
			protected void populateItem(Item<InputGraph> item) {
				final InputGraph inputGraph = item.getModelObject(); 

				item.setModel(new CompoundPropertyModel<InputGraph>(inputGraph));

				item.add(new Label("UUID", ODCSInternal.dataGraphUriPrefix + inputGraph.UUID));
				item.add(new Label("stateLabel"));
				item.add(new Label("engineUUID") {

					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return showEngineUUID;
					}
					
				});
				item.add(new Label("pipelineLabel"));
				item.add(new Label("isInCleanDB"));
				item.add(new Label("updated"));
				
				item.add(new Link<InputGraph>("rerunGraph") {

					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return inputGraph.getStateLabel().equals(EnumGraphState.FINISHED.name());
					}

					@Override
					public void onClick() {
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
				
				item.add(new DeleteButton<InputGraph>(inputGraphDao, inputGraph.getId(), "graph",
						new DeleteConfirmationMessage("graph"), InputGraphsPage.this) {

					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return inputGraph.getStateLabel().equals(EnumGraphState.FINISHED.name()) 
							|| inputGraph.getStateLabel().equals(EnumGraphState.WRONG.name());
					}

					@Override
					public void delete() throws Exception {
						engineOperationsDao.queueGraphForDeletion(inputGraph.getId());
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
}