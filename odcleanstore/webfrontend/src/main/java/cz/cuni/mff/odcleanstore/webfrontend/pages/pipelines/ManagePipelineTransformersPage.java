package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

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
		addPipelineInformationSection(pipelineId);
		addAssignmentSection(pipelineId);
	}

	/*
	 	=======================================================================
	 	Rule information section
	 	=======================================================================
	*/

	private void addPipelineInformationSection(final Long pipelineId)
	{
		setDefaultModel(createModelForOverview(pipelineDao, pipelineId));
		
		add(new Label("label"));
		add(new Label("description"));
		add(new Label("runOnCleanDB"));
	}
	
	/*
	 	=======================================================================
	 	Implementace assignmentTable
	 	=======================================================================
	*/
	
	private void addAssignmentSection(final Long pipelineId) 
	{
		add(
			createGoToPageButton(
				NewAssignmentPage.class, 
				pipelineId, 
				"newAssignmentLink"
			)
		);
		
		addAssignmentTable(pipelineId);
	}
	
	private void addAssignmentTable(final Long pipelineId) 
	{
		IModel<List<TransformerInstance>> model = new LoadableDetachableModel<List<TransformerInstance>>() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TransformerInstance> load() 
			{
				return pipelineDao.load(pipelineId).getTransformers();
			}
		};
		
		ListView<TransformerInstance> listView = new ListView<TransformerInstance>("assignmentTable", model)
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
