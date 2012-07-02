package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class PipelineDetailPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Pipeline> pipelineDao;
	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;
	private DaoForEntityWithSurrogateKey<TransformerInstance> transformerInstanceDao;

	public PipelineDetailPage(final Long pipelineId) 
	{
		super(
			"Home > Pipelines > Assigned transformers", 
			"Manage transformers assignment"
		);
		
		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PipelineDao.class);
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		transformerInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerInstanceDao.class);
		
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
			new RedirectButton(
				NewAssignmentPage.class, 
				pipelineId, 
				"newAssignmentLink"
			)
		);
		
		addAssignmentTable(pipelineId);
	}
	
	private void addAssignmentTable(final Long pipelineId) 
	{
		IDataProvider<TransformerInstance> data = new TransformerInstanceDataProvider(transformerInstanceDao, pipelineId);
		
		DataView<TransformerInstance> dataView = new DataView<TransformerInstance>("assignmentTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TransformerInstance> item) 
			{
				TransformerInstance transformerInstance = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<TransformerInstance>(transformerInstance));
				
				Transformer transformer = transformerDao.load(transformerInstance.getTransformerId());
				item.add(new Label("label", transformer.getLabel()));
				
				item.add(new Label("workDirPath"));	
				item.add(new Label("configuration"));
				item.add(new Label("priority"));
				
				item.add(
					new DeleteButton<TransformerInstance>
					(
						transformerInstanceDao,
						transformer.getId(),
						"assignment",
						new DeleteConfirmationMessage("transformer instance"),
						PipelineDetailPage.this
					)
				);
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
