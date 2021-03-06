package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedLink;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.BooleanLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * List-all-registered-pipelines page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class PipelinesListPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(PipelinesListPage.class);
	
	private PipelineDao pipelineDao;
	private EngineOperationsDao engineOperationsDao;

	/**
	 * 
	 */
	public PipelinesListPage() 
	{
		super
		(
			"Home > Backend > Pipelines > List", 
			"List all pipelines"
		);
		
		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDao(PipelineDao.class);
		engineOperationsDao = daoLookupFactory.getDao(EngineOperationsDao.class);
		
		// register page components
		//
		addHelpWindow(new PipelineHelpPanel("content"));
		addPipelinesTable();
	}
	
	/**
	 * 
	 */
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
				item.add(new Label("authorName"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new BooleanLabel("isLocked"));
				item.add(new BooleanLabel("isDefault")
				{
					private static final long serialVersionUID = 1L;
					
					@Override
					public String convertToString(Boolean value, Locale locale)
					{
						if (Boolean.FALSE.equals(value))
							return "";
						return super.convertToString(value, locale);
					}
				});
				
				item.add(
					new DeletePipelineButton
					(
						pipelineDao,
						pipeline,
						"pipeline",
						new DeleteConfirmationMessage("pipeline", "transformer assignment"),
						PipelinesListPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						PipelineDetailPage.class,
						pipeline.getId(), 
						"showEditPipelinePage"
					)
				);
				
				addMarkPipelineDefaultButton(item, pipeline);
				addToggleLockButton(item, pipeline, true);
				addToggleLockButton(item, pipeline, false);
				addRerunAssociatedGraphsButton(item, pipeline);
			}
		};

		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<Pipeline>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<Pipeline>("sortByAuthor", "username", data, dataView));
		add(new SortTableButton<Pipeline>("sortByIsDefault", "isDefault", data, dataView));
		add(new SortTableButton<Pipeline>("sortByIsLocked", "isLocked", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
	/**
	 * 
	 * @param item
	 * @param pipeline
	 */
	private void addMarkPipelineDefaultButton(Item<Pipeline> item, final Pipeline pipeline)
	{
		Link<String> button = new AuthorizedLink<String>("markPipelineDefault", new Model<String>("XXX"), AuthorizationHelper.isAuthorizedForSettingDefaultPipeline())
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClickAuthorized()
            {
				pipeline.setDefault(true);
				
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
						"The pipeline could not be marked as default due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The pipeline was successfuly marked as default.");
            }
        };

        button.add(
        	new ConfirmationBoxRenderer(
        		"Are you sure you want to mark the pipeline as default?"
        	)
        );
        
		item.add(button);
	}
	
	/**
	 * 
	 * @param item
	 * @param pipeline
	 * @param lock
	 */
	private void addToggleLockButton(Item<Pipeline> item, final Pipeline pipeline, final boolean lock)
	{
		final String status = lock ? "lock" : "unlock";
		item.add(new Link<String>(status + "Pipeline")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
				if (!AuthorizationHelper.isAuthorizedForEntityEditing(pipeline)) 
				{
					return;
				}
				
				pipeline.setLocked(lock);
				
				try 
				{
					pipelineDao.update(pipeline);
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error("The pipeline could not be " + status + "ed due to an unexpected error.");
					return;
				}
				
				getSession().info("The pipeline was successfuly " + status + "ed.");
				setResponsePage(PipelinesListPage.class);
            }
			
			@Override
			public boolean isVisible()
			{
				return pipeline.isLocked() != lock && pipeline.isLocked() != null && AuthorizationHelper.isAuthorizedForEntityEditing(pipeline);
			}
        });
	}
	
	/**
	 * 
	 * @param item
	 * @param pipeline
	 */
	private void addRerunAssociatedGraphsButton(final Item<Pipeline> item, final Pipeline pipeline)
	{
		Link<String> button = new AuthorizedLink<String>("rerunAssociatedGraphs", AuthorizationHelper.isAuthorizedForEntityEditing(pipeline))
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClickAuthorized()
            {
				try {
					engineOperationsDao.rerunGraphsForPipeline(pipeline.getId());
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
				//setResponsePage(PipelinesListPage.class);
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

/**
 * A button to delete a pipeline (a pipeline can only be deleted if it is not currently 
 * marked as default).
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
class DeletePipelineButton extends AuthorizedDeleteButton<Pipeline>
{
	private static final long serialVersionUID = 1L;

	private Pipeline pipeline;
	
	/**
	 * 
	 * @param dao
	 * @param entity
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public DeletePipelineButton(DaoForEntityWithSurrogateKey<Pipeline> dao, Pipeline pipeline, String objName,
		DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(dao, pipeline, objName, message, redirectPage);
		
		this.pipeline = pipeline;
	}
	
	@Override
	public boolean isVisible()
	{
		return super.isVisible() && !pipeline.isDefault();
	}
}
