package cz.cuni.mff.odcleanstore.webfrontend.pages.engine;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.model.EnumGraphState;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.AttachedEngine;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInErrorCount;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.BooleanLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.AttachedEngineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.GraphInErrorCountDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.GraphsInErrorListPage;

@AuthorizeInstantiation({ Role.PIC, Role.ADM })
public class EngineStatePage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private AttachedEngineDao attachedEngineDao;
	private GraphInErrorCountDao graphInErrorCountDao;
	
	// Disable showing engine UUID while there is only one engine instance allowed
	// May change in the future
	private static final boolean showEngineUUID = false;
	
	public EngineStatePage() 
	{
		super
		(
			"Home > Backend > Engine > State", 
			"State"
		);
		
		// prepare DAO objects
		//
		attachedEngineDao = daoLookupFactory.getDao(AttachedEngineDao.class);
		graphInErrorCountDao = daoLookupFactory.getDao(GraphInErrorCountDao.class);
		
		// register page components
		//
		addHelpWindow(new EngineStateHelpPanel("content"));
		addAttachedEngineStatus();
		addGraphsInErrorPerPipeline();
	}

	private void addAttachedEngineStatus()
	{
		DependentSortableDataProvider<AttachedEngine> data =
			new DependentSortableDataProvider<AttachedEngine>(attachedEngineDao, "uuid");
		
		DataView<AttachedEngine> dataView = new DataView<AttachedEngine>("pipelineState", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<AttachedEngine> item) {
				AttachedEngine attachedEngine = item.getModelObject(); 

				item.setModel(new CompoundPropertyModel<AttachedEngine>(attachedEngine));

				item.add(new Label("uuid") {
					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return showEngineUUID;
					}
				});
				item.add(new BooleanLabel("isPipelineError"));
				item.add(new BooleanLabel("isNotifyRequired"));
				item.add(new Label("stateDescription"));
				item.add(new Label("updated"));
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);

		add(new SortTableButton<AttachedEngine>("sortByUUID", "uuid", data, dataView) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isVisible() {
				return showEngineUUID;
			}
		});
		add(new SortTableButton<AttachedEngine>("sortByError", "isPipelineError", data, dataView));
		add(new SortTableButton<AttachedEngine>("sortByNotificationRequired", "isNotifyRequired", data, dataView));
		add(new SortTableButton<AttachedEngine>("sortByUpdated", "updated", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}

	private void addGraphsInErrorPerPipeline() {
		DependentSortableDataProvider<GraphInErrorCount> data =
				new DependentSortableDataProvider<GraphInErrorCount>(graphInErrorCountDao, "pipelineLabel", "iState.label", EnumGraphState.WRONG.name());
			
		DataView<GraphInErrorCount> dataView = new DataView<GraphInErrorCount>("graphsInErrorPerPipeline", data)
		{
			private static final long serialVersionUID = 1L;
				
			@Override
			protected void populateItem(Item<GraphInErrorCount> item) {
				GraphInErrorCount graphInErrorCount = item.getModelObject(); 

				item.setModel(new CompoundPropertyModel<GraphInErrorCount>(graphInErrorCount));

				item.add(new Label("pipelineLabel"));
				item.add(new Label("graphCount"));
				
				item.add(new RedirectWithParamButton(GraphsInErrorListPage.class,
						"seeMore",
						"pipelineId",
						graphInErrorCount.pipelineId));
			}
		};
		
		add(new SortTableButton<GraphInErrorCount>("sortByPipelineLabel", "pipelineLabel", data, dataView));
		add(new SortTableButton<GraphInErrorCount>("sortByGraphCount", "graphCount", data, dataView));
			
		add(dataView);
	}
}
