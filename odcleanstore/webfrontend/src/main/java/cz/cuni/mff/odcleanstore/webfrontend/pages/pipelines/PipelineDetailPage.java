package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedLink;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedRedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.BooleanLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.GraphInErrorDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Pipeline-overview page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class PipelineDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PipelineDetailPage.class);

	private enum MoveDirection
	{
		UP(-1, "Up"),
		DOWN(+1, "Down");
		
		private int shift;
		private String label;

		private MoveDirection(int shift, String label)
		{
			this.shift = shift;
			this.label = label;
		}
		
		public int getShift()
		{
			return shift;
		}
		
		public String getLabel()
		{
			return label;
		}
	}
	
	private PipelineDao pipelineDao;
	private TransformerInstanceDao transformerInstanceDao;
	private GraphInErrorDao graphInErrorDao;
	
	/**
	 * 
	 * @param pipelineId
	 */
	public PipelineDetailPage(final Integer pipelineId) 
	{
		super
		(
			"Home > Backend > Pipelines > Edit", 
			"Edit a pipeline",
			PipelineDao.class,
			pipelineId
		);
		

		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDao(PipelineDao.class);
		transformerInstanceDao = daoLookupFactory.getDao(TransformerInstanceDao.class);
		graphInErrorDao = daoLookupFactory.getDao(GraphInErrorDao.class);
		
		// prepare additional information
		//
		QueryCriteria pipelineIdCriteria = new QueryCriteria();
		
		pipelineIdCriteria.addWhereClause("pipelineId", pipelineId);

		int errors = graphInErrorDao.loadAllBy(pipelineIdCriteria).size();
		
		// register page components
		//
		addViewGraphsInError("viewGraphsInError", pipelineId, errors);
		addHelpWindow("pipelineHelpWindow", "openPipelineHelpWindow", new PipelineHelpPanel("content"));
		addHelpWindow("transformerInstanceHelpWindow", "openTransformerInstanceHelpWindow", new TransformerInstanceHelpPanel("content"));
		addNewPipelineForm(pipelineId);
		addPipelineInformationSection(pipelineId);
		addAssignmentSection(pipelineId);
	}
	
	/**
	 * 
	 * @param compName
	 * @param pipelineId
	 * @param errors
	 */
	private void addViewGraphsInError(final String compName, final Integer pipelineId, final Integer errors) {
		add(new RedirectWithParamButton(GraphsInErrorListPage.class, compName, "pipelineId", pipelineId) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isVisible() {
				return errors > 0;
			}
		});
	}
	
	/**
	 * 
	 * @param pipelineId
	 */
	private void addNewPipelineForm(final Integer pipelineId)
	{
		Pipeline pipeline = pipelineDao.load(pipelineId);
		IModel<Pipeline> formModel = new CompoundPropertyModel<Pipeline>(pipeline);
		
		Form<Pipeline> form = new LimitedEditingForm<Pipeline>("editPipelineForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				Pipeline pipeline = this.getModelObject();
				
				try {
					pipelineDao.update(pipeline);
				}
				catch (DaoException ex)
				{	
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);					
					getSession().error(
						"The pipeline could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The pipeline was successfuly updated.");
				//setResponsePage(PipelinesListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
	
	/**
	 * 
	 * @param pipelineId
	 */
	private void addPipelineInformationSection(final Integer pipelineId)
	{
		setDefaultModel(createModelForOverview(pipelineDao, pipelineId));
		
		add(new BooleanLabel("isDefault"));
	}

	/**
	 * 
	 * @param pipelineId
	 */
	private void addAssignmentSection(final Integer pipelineId) 
	{
		add(
			new AuthorizedRedirectButton(
				NewTransformerAssignmentPage.class, 
				pipelineId,
				isEditable(),
				"newAssignmentLink"
			)
		);
		
		addAssignmentTable(pipelineId);
	}
	
	/**
	 * 
	 * @param pipelineId
	 */
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
				item.add(new BooleanLabel("runOnCleanDB"));
				item.add(new Label("priority"));
				
				item.add(
					new AuthorizedDeleteButton<TransformerInstance>
					(
						transformerInstanceDao,
						transformerInstance.getId(),
						isEditable(),
						"assignment",
						new DeleteConfirmationMessage("transformer instance"),
						PipelineDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						TransformerAssignmentDetailPage.class,
						transformerInstance.getId(),
						"showEditTransformerInstancePage"
					)
				);
				
				addMoveButton(item, MoveDirection.UP);
				addMoveButton(item, MoveDirection.DOWN);
			}


		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<TransformerInstance>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<TransformerInstance>("sortByRunOnCleanDB", "runOnCleanDB", data, dataView));
		add(new SortTableButton<TransformerInstance>("sortByPriority", "priority", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}	
	
	/**
	 * 
	 * @param item
	 * @param direction
	 */
	private void addMoveButton(final Item<TransformerInstance> item, final MoveDirection direction)
	{
		item.add(new AuthorizedLink<String>("move" + direction.getLabel(), isEditable()) 
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onClickAuthorized()
			{
				TransformerInstance instance = item.getModelObject();
				int newPriority = instance.getPriority() + direction.getShift();
				int instanceCount = transformerInstanceDao.getInstancesCount(instance.getPipelineId());
				newPriority = Math.min(instanceCount, Math.max(newPriority, 1));
				instance.setPriority(newPriority);
				
				try 
				{
					transformerInstanceDao.update(instance);
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					return;
				}
			}
		});
	}
}
