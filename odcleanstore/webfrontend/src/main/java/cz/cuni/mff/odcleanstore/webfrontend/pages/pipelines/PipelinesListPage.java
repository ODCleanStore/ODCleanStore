package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class PipelinesListPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(PipelinesListPage.class);
	
	private DaoForEntityWithSurrogateKey<Pipeline> pipelineDao;

	public PipelinesListPage() 
	{
		super
		(
			"Home > Pipelines > List", 
			"Registered pipelines configuration"
		);
		
		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PipelineDao.class);
		
		// register page components
		//
		addPipelinesTable();
	}

	/*
	 	=======================================================================
	 	Implementace pipelinesTable
	 	=======================================================================
	*/
	
	private void addPipelinesTable()
	{
		IDataProvider<Pipeline> data = new DataProvider<Pipeline>(pipelineDao);
		
		DataView<Pipeline> dataView = new DataView<Pipeline>("pipelinesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<Pipeline> item) 
			{
				Pipeline pipeline = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Pipeline>(pipeline));

				item.add(new Label("label"));
				item.add(new Label("description"));
				item.add(new Label("runOnCleanDB"));
				
				item.add(
					new DeleteButton<Pipeline>
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
				
				addMakePipelineRunOnCleanDBButton(item, pipeline);
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
	private void addMakePipelineRunOnCleanDBButton(Item<Pipeline> item, final Pipeline pipeline)
	{
		Link button = new Link("makePipelineRunOnCleanDB")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
				pipeline.setRunOnCleanDB(true);
				
				try {
					pipelineDao.update(pipeline);
				}
				catch (Exception ex)
				{
					// logger.error(ex.getMessage());
					
					getSession().error(
						"The pipeline could not be marked to be run on the clean DB due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The pipeline was successfuly marked to be run on the clean DB.");
				setResponsePage(PipelinesListPage.class);
            }
        };
        
		item.add(button);
	}
}
