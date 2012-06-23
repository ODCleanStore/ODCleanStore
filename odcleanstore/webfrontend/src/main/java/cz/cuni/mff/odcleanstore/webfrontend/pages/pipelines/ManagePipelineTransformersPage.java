package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class ManagePipelineTransformersPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Pipeline> pipelineDao;
	private TransformerInstanceDao transformerInstanceDao;

	public ManagePipelineTransformersPage(final Long pipelineId) 
	{
		super(
			"Home > Pipelines > Assigned transformers", 
			"Manage transformers assignment"
		);
		
		// prepare DAO objects
		//
		pipelineDao = (DaoForEntityWithSurrogateKey<Pipeline>) daoLookupFactory.getDao(PipelineDao.class);
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
		TransformerInstanceDataProvider data = new TransformerInstanceDataProvider(
			transformerInstanceDao, 
			pipelineId
		);
		
		DataView<TransformerInstance> dataView = new DataView<TransformerInstance>("assignmentTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TransformerInstance> item) 
			{
				TransformerInstance transformer = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<TransformerInstance>(transformer));
				
				item.add(new Label("label"));
				item.add(new Label("workDirPath"));	
				item.add(new Label("configuration"));
				item.add(new Label("priority"));
				
				addDeleteButton(item, transformer.getPipelineId(), transformer.getTransformerId());	
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
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
