package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class PipelinesManagementPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(PipelinesManagementPage.class);
	
	private Dao<Pipeline> pipelineDao;

	public PipelinesManagementPage() 
	{
		super
		(
			"Home > Pipelines", 
			"Registered pipelines configuration"
		);
		
		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDao(PipelineDao.class);
		
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
		List<Pipeline> allPipelines = pipelineDao.loadAll();
		
		ListView<Pipeline> listView = new ListView<Pipeline>("pipelinesTable", allPipelines)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<Pipeline> item) 
			{
				final Pipeline pipeline = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Pipeline>(pipeline));

				item.add(new Label("label"));
				item.add(new Label("description"));
				item.add(new Label("runOnCleanDB"));
				
				addDeleteButton(item, pipeline);
				addManagePipelineTransformersButton(item, pipeline.getId());
				addMakePipelineRunOnCleanDBButton(item, pipeline);
			}
		};
		
		add(listView);
	}
	
	private void addDeleteButton(ListItem<Pipeline> item, final Pipeline pipeline)
	{
		Link button = new Link("deletePipeline")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
            	pipelineDao.delete(pipeline);
            	
				getSession().info("The pipeline was successfuly deleted.");
				setResponsePage(PipelinesManagementPage.class);
            }
        };

	    button.add(new ConfirmationBoxRenderer("Are you sure you want to delete the pipeline?"));
	    
		item.add(button);
	}
	
	private void addManagePipelineTransformersButton(ListItem<Pipeline> item, final Long pipelineId)
	{
		Link button = new Link("managePipelineTransformers")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
            	setResponsePage(
            		new ManagePipelineTransformersPage(pipelineId)
				);
            }
        };
	    
		item.add(button);
	}
	
	private void addMakePipelineRunOnCleanDBButton(ListItem<Pipeline> item, final Pipeline pipeline)
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
				setResponsePage(PipelinesManagementPage.class);
            }
        };
        
		item.add(button);
	}
}
