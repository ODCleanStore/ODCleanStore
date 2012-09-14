package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.OfficialPipelinesDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "POC" })
public class PipelinesListPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(PipelinesListPage.class);
	
	private DaoForEntityWithSurrogateKey<Pipeline> pipelineDao;
	private OfficialPipelinesDao officialPipelinesDao;
	private EngineOperationsDao engineOperationsDao;

	public PipelinesListPage() 
	{
		super
		(
			"Home > Backend > Pipelines > List", 
			"List all pipelines"
		);
		
		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PipelineDao.class);
		officialPipelinesDao = daoLookupFactory.getOfficialPipelinesDao();
		engineOperationsDao = daoLookupFactory.getEngineOperationsDao();
		
		// register page components
		//
		addHelpWindow(new PipelineHelpPanel("content"));
		addPipelinesTable();
	}
	
	private void addPipelinesTable()
	{
		SortableDataProvider<Pipeline> data = new GenericSortableDataProvider<Pipeline>(pipelineDao, "label");
		
		DataView<Pipeline> dataView = new DataView<Pipeline>("pipelinesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<Pipeline> item) 
			{
				Pipeline pipeline = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Pipeline>(pipeline));

				item.add(new Label("label"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new Label("isDefault"));
				
				item.add(
					new DeleteRawButton<Pipeline>
					(
						pipelineDao,
						pipeline.getId(),
						"pipeline",
						new DeleteConfirmationMessage("pipeline", "transformer assignment"),
						PipelinesListPage.this
					)
				);
				
				item.add(
					new RedirectButton(
						PipelineDetailPage.class,
						pipeline.getId(), 
						"managePipelineTransformers"
					)
				);
				
				item.add(
					new RedirectButton(
						EditPipelinePage.class,
						pipeline.getId(), 
						"showEditPipelinePage"
					)
				);
				
				addMarkPipelineDefaultButton(item, pipeline);
				addRerunAssociatedGraphsButton(item, pipeline.getId());
			}
		};

		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<Pipeline>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<Pipeline>("sortByIsDefault", "isDefault", data, dataView));
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
	private void addMarkPipelineDefaultButton(Item<Pipeline> item, final Pipeline pipeline)
	{
		Link button = new Link("markPipelineDefault")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
				pipeline.setDefault(true);
				
				try {
					pipelineDao.update(pipeline);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// logger.error(ex.getMessage());
					
					getSession().error(
						"The pipeline could not be marked as default due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The pipeline was successfuly marked as default.");
				setResponsePage(PipelinesListPage.class);
            }
        };

        button.add(
        	new ConfirmationBoxRenderer(
        		"Are you sure you want to mark the pipeline as default?"
        	)
        );
        
		item.add(button);
	}
	
	private void addRerunAssociatedGraphsButton(final Item<Pipeline> item, final Long pipelineId)
	{
		Link button = new Link("rerunAssociatedGraphs")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
				try {
					engineOperationsDao.rerunGraphsForPipeline(pipelineId);
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					
					getSession().error(
						"The associated graphs could not be marked to be rerun due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The associated graphs were successfuly marked to be rerun.");
				setResponsePage(PipelinesListPage.class);
            }
        };

        button.add(
        	new ConfirmationBoxRenderer(
        		"Are you sure you want to rerun all associated graphs?"
        	)
        );
        
		item.add(button);
	}
}
