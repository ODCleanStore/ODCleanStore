package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC, Role.ADM_PIC })
public class PipelineDetailPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Pipeline> pipelineDao;
	private DaoForEntityWithSurrogateKey<TransformerInstance> transformerInstanceDao;

	public PipelineDetailPage(final Integer pipelineId) 
	{
		super(
			"Home > Backend > Pipelines > Detail", 
			"Show pipeline detail"
		);
		
		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PipelineDao.class);
		transformerInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerInstanceDao.class);
		
		// register page components
		//
		addHelpWindow("pipelineHelpWindow", "openPipelineHelpWindow", new PipelineHelpPanel("content"));
		addHelpWindow("transformerInstanceHelpWindow", "openTransformerInstanceHelpWindow", new TransformerInstanceHelpPanel("content"));
		addPipelineInformationSection(pipelineId);
		addAssignmentSection(pipelineId);
	}

	private void addPipelineInformationSection(final Integer pipelineId)
	{
		setDefaultModel(createModelForOverview(pipelineDao, pipelineId));
		
		add(new Label("label"));
		add(new Label("description"));
		add(new Label("isDefault"));
	}

	private void addAssignmentSection(final Integer pipelineId) 
	{
		add(
			new RedirectWithParamButton(
				NewTransformerAssignmentPage.class, 
				pipelineId, 
				"newAssignmentLink"
			)
		);
		
		addAssignmentTable(pipelineId);
	}
	
	private void addAssignmentTable(final Integer pipelineId) 
	{
		SortableDataProvider<TransformerInstance> data = new DependentSortableDataProvider<TransformerInstance>
		(
			transformerInstanceDao,
			"priority",
			"pipelineId", 
			pipelineId
		);

		DataView<TransformerInstance> dataView = new DataView<TransformerInstance>("assignmentTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<TransformerInstance> item) 
			{
				TransformerInstance transformerInstance = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<TransformerInstance>(transformerInstance));
				
				item.add(new Label("label"));	
				item.add(new TruncatedLabel("configuration", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new Label("runOnCleanDB"));
				item.add(new Label("priority"));
				
				item.add(
					new DeleteRawButton<TransformerInstance>
					(
						transformerInstanceDao,
						transformerInstance.getId(),
						"assignment",
						new DeleteConfirmationMessage("transformer instance"),
						PipelineDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						TransformerInstanceDetailPage.class,
						transformerInstance.getId(),
						"showTransformerInstanceDetailPage"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditTransformerAssignmentPage.class,
						transformerInstance.getId(),
						"showEditTransformerInstancePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<TransformerInstance>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<TransformerInstance>("sortByRunOnCleanDB", "runOnCleanDB", data, dataView));
		add(new SortTableButton<TransformerInstance>("sortByPriority", "priority", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}
