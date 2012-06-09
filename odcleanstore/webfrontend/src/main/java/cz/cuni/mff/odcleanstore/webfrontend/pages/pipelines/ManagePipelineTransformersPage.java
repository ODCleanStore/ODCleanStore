package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class ManagePipelineTransformersPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private Dao<Pipeline> pipelineDao;
	private TransformerInstanceDao transformerInstanceDao;

	public ManagePipelineTransformersPage(final Long pipelineId) 
	{
		super(
			"Home > Pipelines > Assigned transformers", 
			"Manage transformers assignment"
		);
		
		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDao(PipelineDao.class);
		transformerInstanceDao = daoLookupFactory.getTransformerInstanceDao();
		
		// register page components
		//
		final Pipeline pipeline = pipelineDao.load(pipelineId);
		
		addPipelineInformationSection(pipeline);
		addAssignmentSection(pipeline);
	}

	/*
	 	=======================================================================
	 	Rule information section
	 	=======================================================================
	*/

	private void addPipelineInformationSection(final Pipeline pipeline)
	{
		setDefaultModel(new CompoundPropertyModel<Pipeline>(pipeline));
		
		add(new Label("label"));
		add(new Label("description"));
		add(new Label("runOnCleanDB"));
	}
	
	/*
	 	=======================================================================
	 	Implementace assignmentTable
	 	=======================================================================
	*/
	
	private void addAssignmentSection(Pipeline pipeline) 
	{
		addNewAssignmentLink(pipeline.getId());
		addAssignmentTable(pipeline);
	}

	private void addNewAssignmentLink(final Long pipelineId)
	{
		add(new Link("newAssignmentLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() 
			{
				setResponsePage(
					new NewAssignmentPage(pipelineId)
				);
			}
		});
	}
	
	private void addAssignmentTable(final Pipeline pipeline) 
	{
		List<TransformerInstance> assignment = pipeline.getTransformers();
		
		ListView<TransformerInstance> listView = new ListView<TransformerInstance>("assignmentTable", assignment)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<TransformerInstance> item) 
			{
				final TransformerInstance transformer = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<TransformerInstance>(transformer));
				
				item.add(new Label("label"));
				item.add(new Label("workDirPath"));	
				item.add(new Label("configuration"));
				item.add(new Label("priority"));
				
				addDeleteButton(item, transformer.getPipelineId(), transformer.getTransformerId());
			}
		};
		
		add(listView);
	}
	
	private void addDeleteButton(ListItem<TransformerInstance> item, final Long pipelineId, final Long transformerId)
	{
		Link button = new Link("deleteAssignment")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
				transformerInstanceDao.delete(pipelineId, transformerId);
            	
				getSession().info("The assignment was successfuly deleted.");
				setResponsePage(new ManagePipelineTransformersPage(pipelineId));
            }
        };

	    button.add(new ConfirmationBoxRenderer("Are you sure you want to delete the assignment?"));
		item.add(button);
	}
}
